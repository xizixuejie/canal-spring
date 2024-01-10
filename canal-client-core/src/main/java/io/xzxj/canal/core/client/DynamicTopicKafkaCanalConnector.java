/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.xzxj.canal.core.client;

import com.alibaba.fastjson2.JSON;
import com.alibaba.otter.canal.client.CanalMQConnector;
import com.alibaba.otter.canal.client.kafka.MessageDeserializer;
import com.alibaba.otter.canal.protocol.FlatMessage;
import com.alibaba.otter.canal.protocol.Message;
import com.alibaba.otter.canal.protocol.exception.CanalClientException;
import com.google.common.collect.Lists;
import io.xzxj.canal.core.context.MqTopicMap;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 支持订阅多个topic的KafkaCanalConnector
 */
public class DynamicTopicKafkaCanalConnector implements CanalMQConnector {

    protected KafkaConsumer<String, Message> kafkaConsumer;
    protected KafkaConsumer<String, String> kafkaConsumer2; // 用于扁平message的数据消费

    private final Map<String, String> dynamicTopic;
    private final List<String> topics;

    protected Properties properties;
    protected volatile boolean connected = false;
    protected volatile boolean running = false;
    protected boolean flatMessage;

    private final Map<TopicPartitionKey, Long> currentOffsets = new ConcurrentHashMap<>();

    public DynamicTopicKafkaCanalConnector(String servers,
                                           Map<String, String> dynamicTopic,
                                           List<String> topics,
                                           String groupId,
                                           Integer batchSize,
                                           boolean flatMessage) {
        this.flatMessage = flatMessage;

        properties = new Properties();
        properties.put("bootstrap.servers", servers);
        properties.put("group.id", groupId);
        properties.put("enable.auto.commit", false);
        properties.put("auto.commit.interval.ms", "1000");
        properties.put("auto.offset.reset", "latest"); // 如果没有offset则从最后的offset开始读
        properties.put("request.timeout.ms", "40000"); // 必须大于session.timeout.ms的设置
        properties.put("session.timeout.ms", "30000"); // 默认为30秒
        properties.put("isolation.level", "read_committed");
        if (batchSize == null) {
            batchSize = 100;
        }
        properties.put("max.poll.records", batchSize.toString());
        properties.put("key.deserializer", StringDeserializer.class.getName());
        if (!flatMessage) {
            properties.put("value.deserializer", MessageDeserializer.class.getName());
        } else {
            properties.put("value.deserializer", StringDeserializer.class.getName());
        }

        this.dynamicTopic = dynamicTopic;
        this.topics = topics;
    }

    @Override
    public void connect() {
        if (connected) {
            return;
        }

        connected = true;
        if (kafkaConsumer == null && !flatMessage) {
            kafkaConsumer = new KafkaConsumer<>(properties);

        }
        if (kafkaConsumer2 == null && flatMessage) {
            kafkaConsumer2 = new KafkaConsumer<>(properties);
        }
    }

    @Override
    public void disconnect() {
        if (kafkaConsumer != null) {
            kafkaConsumer.close();
            kafkaConsumer = null;
        }
        if (kafkaConsumer2 != null) {
            kafkaConsumer2.close();
            kafkaConsumer2 = null;
        }

        connected = false;
    }

    protected void waitClientRunning() {
        running = true;
    }

    @Override
    public boolean checkValid() {
        return true;// 默认都放过
    }

    @Override
    public void subscribe() {
        waitClientRunning();
        if (!running) {
            return;
        }

        if (CollectionUtils.isNotEmpty(dynamicTopic.keySet())) {
            List<TopicPartition> topicPartitionList = dynamicTopic.entrySet().stream()
                    .filter(entry -> StringUtils.isNotBlank(entry.getValue()))
                    .flatMap(entry -> {
                        String topic = entry.getKey();
                        String[] partitions = entry.getValue().split(",");
                        return Arrays.stream(partitions)
                                .map(partition -> new TopicPartition(topic, Integer.parseInt(partition.trim())));
                    })
                    .collect(Collectors.toList());

            if (CollectionUtils.isNotEmpty(topicPartitionList)) {
                if (kafkaConsumer != null) {
                    kafkaConsumer.assign(topicPartitionList);
                }
                if (kafkaConsumer2 != null) {
                    kafkaConsumer2.assign(topicPartitionList);
                }
            }
        } else {
            if (kafkaConsumer != null) {
                kafkaConsumer.subscribe(topics);
            }
            if (kafkaConsumer2 != null) {
                kafkaConsumer2.subscribe(topics);
            }
        }
    }

    @Override
    public void unsubscribe() {
        waitClientRunning();
        if (!running) {
            return;
        }

        if (kafkaConsumer != null) {
            kafkaConsumer.unsubscribe();
        }
        if (kafkaConsumer2 != null) {
            kafkaConsumer2.unsubscribe();
        }
    }

    @Override
    public List<Message> getList(Long timeout, TimeUnit unit) throws CanalClientException {
        waitClientRunning();
        if (!running) {
            return Lists.newArrayList();
        }

        List<Message> messages = getListWithoutAck(timeout, unit);
        if (messages != null && !messages.isEmpty()) {
            this.ack();
        }
        return messages;
    }


    @Override
    public List<Message> getListWithoutAck(Long timeout, TimeUnit unit) throws CanalClientException {
        waitClientRunning();
        if (!running) {
            return Lists.newArrayList();
        }

        Duration duration = Duration.of(timeout, ChronoUnit.valueOf(unit.name()));
        ConsumerRecords<String, Message> records = kafkaConsumer.poll(duration);

        currentOffsets.clear();
        for (TopicPartition topicPartition : records.partitions()) {
            currentOffsets.put(new TopicPartitionKey(topicPartition), kafkaConsumer.position(topicPartition));
        }

        if (!records.isEmpty()) {
            List<Message> messages = new ArrayList<>();
            for (ConsumerRecord<String, Message> record : records) {
                Message message = record.value();
                messages.add(message);
                MqTopicMap.setTopic(message.getId(), record.topic(), record.partition());
            }
            return messages;
        }
        return Lists.newArrayList();
    }

    @Override
    public List<FlatMessage> getFlatList(Long timeout, TimeUnit unit) throws CanalClientException {
        waitClientRunning();
        if (!running) {
            return Lists.newArrayList();
        }

        List<FlatMessage> messages = getFlatListWithoutAck(timeout, unit);
        if (messages != null && !messages.isEmpty()) {
            this.ack();
        }
        return messages;
    }

    @Override
    public List<FlatMessage> getFlatListWithoutAck(Long timeout, TimeUnit unit) throws CanalClientException {
        waitClientRunning();
        if (!running) {
            return Lists.newArrayList();
        }

        Duration duration = Duration.of(timeout, ChronoUnit.valueOf(unit.name()));
        ConsumerRecords<String, String> records = kafkaConsumer2.poll(duration);

        currentOffsets.clear();
        for (TopicPartition topicPartition : records.partitions()) {
            currentOffsets.put(new TopicPartitionKey(topicPartition), kafkaConsumer2.position(topicPartition));
        }

        if (!records.isEmpty()) {
            List<FlatMessage> flatMessages = new ArrayList<>();
            for (ConsumerRecord<String, String> record : records) {
                String flatMessageJson = record.value();
                FlatMessage flatMessage = JSON.parseObject(flatMessageJson, FlatMessage.class);
                flatMessages.add(flatMessage);

                MqTopicMap.setTopic(flatMessage.getId(), record.topic(), record.partition());
            }
            return flatMessages;
        }
        return Lists.newArrayList();
    }

    @Override
    public void rollback() {
        waitClientRunning();
        if (!running) {
            return;
        }
        // 回滚所有分区
        rollback(kafkaConsumer);
        rollback(kafkaConsumer2);
    }

    private void rollback(KafkaConsumer<String, ?> kafkaConsumer) {
        if (kafkaConsumer == null) {
            return;
        }

        for (Map.Entry<TopicPartitionKey, Long> entry : currentOffsets.entrySet()) {
            TopicPartition topicPartition = entry.getKey().toTopicPartition();
            kafkaConsumer.seek(topicPartition, entry.getValue() - 1);
        }
    }

    @Override
    public void ack() {
        waitClientRunning();
        if (!running) {
            return;
        }

        if (kafkaConsumer != null) {
            kafkaConsumer.commitSync();
        }
        if (kafkaConsumer2 != null) {
            kafkaConsumer2.commitSync();
        }
    }

    @Override
    public void subscribe(String filter) throws CanalClientException {
        throw new CanalClientException("mq not support this method");
    }

    @Override
    public Message get(int batchSize) throws CanalClientException {
        throw new CanalClientException("mq not support this method");
    }

    @Override
    public Message get(int batchSize, Long timeout, TimeUnit unit) throws CanalClientException {
        throw new CanalClientException("mq not support this method");
    }

    @Override
    public Message getWithoutAck(int batchSize) throws CanalClientException {
        throw new CanalClientException("mq not support this method");
    }

    @Override
    public Message getWithoutAck(int batchSize, Long timeout, TimeUnit unit) throws CanalClientException {
        throw new CanalClientException("mq not support this method");
    }

    @Override
    public void ack(long batchId) throws CanalClientException {
        throw new CanalClientException("mq not support this method");
    }

    @Override
    public void rollback(long batchId) throws CanalClientException {
        throw new CanalClientException("mq not support this method");
    }

    /**
     * 重新设置sessionTime
     *
     * @param timeout
     * @param unit
     */
    public void setSessionTimeout(Long timeout, TimeUnit unit) {
        long t = unit.toMillis(timeout);
        properties.put("request.timeout.ms", String.valueOf(t + 60000));
        properties.put("session.timeout.ms", String.valueOf(t));
    }

    static class TopicPartitionKey extends MultiKey<Object> {
        private final String topic;
        private final Integer partition;

        public TopicPartitionKey(TopicPartition topicPartition) {
            super(topicPartition.topic(), topicPartition.partition());
            this.topic = topicPartition.topic();
            this.partition = topicPartition.partition();
        }

        public TopicPartitionKey(String topic, Integer partition) {
            super(topic, partition);
            this.topic = topic;
            this.partition = partition;
        }

        public TopicPartition toTopicPartition() {
            return new TopicPartition(topic, partition);
        }

        public String getTopic() {
            return topic;
        }

        public Integer getPartition() {
            return partition;
        }
    }

}
