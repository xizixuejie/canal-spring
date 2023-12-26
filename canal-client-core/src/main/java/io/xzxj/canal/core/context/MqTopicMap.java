package io.xzxj.canal.core.context;

import org.apache.kafka.common.TopicPartition;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 存放canal messageId和TopicPartition对应关系
 */
public final class MqTopicMap {

    private static final Map<Long, TopicPartition> TOPIC_PARTITION_MAP = new ConcurrentHashMap<>(16);

    @Nullable
    public static TopicPartition getTopic(Long id) {
        return TOPIC_PARTITION_MAP.get(id);
    }

    public static void setTopic(Long id, String topic, int partition) {
        TOPIC_PARTITION_MAP.put(id, new TopicPartition(topic, partition));
    }

    public static void removeTopic(Long id) {
        TOPIC_PARTITION_MAP.remove(id);
    }

}
