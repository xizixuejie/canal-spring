package io.xzxj.canal.core.context;

import com.google.common.collect.Maps;
import io.xzxj.canal.core.annotation.CanalListener;
import io.xzxj.canal.core.annotation.CanalTopicPartition;
import io.xzxj.canal.core.annotation.CanalTopicPartitions;
import io.xzxj.canal.core.listener.EntryListener;
import io.xzxj.canal.core.metadata.AbstractEntityInfoHelper;
import io.xzxj.canal.core.model.ListenerKey;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.common.TopicPartition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class EntryListenerContext {

    private final DatabaseListenerContext databaseListenerContext = DatabaseListenerContext.getInstance();

    private final AbstractEntityInfoHelper entityInfoHelper;

    public EntryListenerContext(AbstractEntityInfoHelper entityInfoHelper,
                                List<EntryListener<?>> entryListenerList) {
        this.entityInfoHelper = entityInfoHelper;
        this.initEntryListenerMap(entryListenerList);
    }

    public List<EntryListener<?>> findEntryListener(Long messageId, String destination, String schemaName, String tableName) {
        TopicPartition topicPartition = MqTopicMap.getTopic(messageId);

        ListenerKey.Builder keybuilder = new ListenerKey.Builder()
                .destination(destination)
                .schemaName(schemaName);
        if (topicPartition != null) {
            keybuilder.topic(topicPartition.topic())
                    .partition(String.valueOf(topicPartition.partition()));
        }
        List<EntryListener<?>> listenerList = databaseListenerContext.getEntryListenersByTableName(keybuilder.build(), tableName);
        MqTopicMap.removeTopic(messageId);
        return listenerList;
    }

    /**
     * 初始化 存放EntryListenerMap的方法
     * key: destination,topic,partition,schemaName
     *
     * @param entryListenerList
     */
    private void initEntryListenerMap(List<EntryListener<?>> entryListenerList) {
        for (EntryListener<?> entryListener : entryListenerList) {
            CanalListener annotation = entryListener.getClass().getAnnotation(CanalListener.class);
            if (annotation == null) {
                continue;
            }
            String[] destinations = annotation.destination();
            Map<String, int[]> dynamicTopic = buildDynamicTopic(entryListener.getClass().getAnnotation(CanalTopicPartitions.class));
            String[] topics = annotation.topics();
            String[] schemaNames = annotation.schemaName();
            String tableName = annotation.tableName();

            if (StringUtils.isBlank(tableName)) {
                tableName = entityInfoHelper.getTableName(entryListener);
            }

            DatabaseListenerContextBuilder contextBuilder = new DatabaseListenerContextBuilder(destinations,
                    dynamicTopic, topics,
                    schemaNames, tableName,
                    entryListener);
            contextBuilder.buildContext();
        }
    }

    private Map<String, int[]> buildDynamicTopic(CanalTopicPartitions canalTopicPartitions) {
        if (canalTopicPartitions == null) {
            return Maps.newHashMap();
        }
        Map<String, int[]> stringMap = new HashMap<>();
        for (CanalTopicPartition topicPartition : canalTopicPartitions.value()) {
            int[] partitions = topicPartition.partitions();
            stringMap.put(topicPartition.topic(), partitions);
        }
        return stringMap;
    }

    class DatabaseListenerContextBuilder {
        private final ListenerKey.Builder keybuilder = new ListenerKey.Builder();

        private final String[] destinations;
        private final Map<String, int[]> dynamicTopic;
        private final String[] topics;
        private final String[] schemaNames;
        private final String tableName;
        private final EntryListener<?> entryListener;

        public DatabaseListenerContextBuilder(String[] destinations,
                                              Map<String, int[]> dynamicTopic,
                                              String[] topics,
                                              String[] schemaNames,
                                              String tableName,
                                              EntryListener<?> entryListener) {
            this.destinations = destinations;
            this.dynamicTopic = dynamicTopic;
            this.topics = topics;
            this.schemaNames = schemaNames;
            this.tableName = tableName;
            this.entryListener = entryListener;
        }

        public void buildContext() {
            buildContextForDestinations();
        }

        private void buildContextForDestinations() {
            if (destinations.length == 0) {
                buildContextForTopics();
            } else {
                for (String destination : destinations) {
                    keybuilder.destination(destination);
                    buildContextForTopics();
                }
            }
        }

        private void buildContextForTopics() {
            if (CollectionUtils.isNotEmpty(dynamicTopic.keySet())) {
                buildContextForTopicPartition();
                return;
            }
            if (topics.length == 0) {
                buildContextForSchema();
            } else {
                for (String topic : topics) {
                    keybuilder.topic(topic);
                    buildContextForSchema();
                }
            }
        }

        private void buildContextForTopicPartition() {
            for (Map.Entry<String, int[]> entry : dynamicTopic.entrySet()) {
                keybuilder.topic(entry.getKey());
                for (Integer partition : entry.getValue()) {
                    keybuilder.partition(String.valueOf(partition));
                    buildContextForSchema();
                }
            }
        }

        private void buildContextForSchema() {
            if (schemaNames.length == 0) {
                databaseListenerContext.put(keybuilder.build(), tableName, entryListener);
            } else {
                for (String schemaName : schemaNames) {
                    databaseListenerContext.put(keybuilder.schemaName(schemaName).build(), tableName, entryListener);
                }
            }
        }

    }

}
