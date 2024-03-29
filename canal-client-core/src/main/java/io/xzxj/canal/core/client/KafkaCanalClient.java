package io.xzxj.canal.core.client;

import com.alibaba.otter.canal.client.CanalMQConnector;
import io.xzxj.canal.core.handler.IMessageHandler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author xzxj
 * @date 2023/3/15 13:36
 */
public class KafkaCanalClient extends AbstractMqCanalClient {

    private static final Logger log = LoggerFactory.getLogger(KafkaCanalClient.class);

    @Override
    public void subscribe() {
        connector.subscribe();
    }

    @Override
    public void handleListening() {
        handleListening(flatMessage);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String filter = StringUtils.EMPTY;
        private Integer batchSize = 1;
        private Long timeout = 1L;
        private TimeUnit unit = TimeUnit.SECONDS;
        private String destination;
        private String servers;
        private List<String> topics;
        private Map<String, String> dynamicTopic;
        private String groupId;
        private Boolean flatMessage = Boolean.TRUE;
        private IMessageHandler<?> messageHandler;

        private Builder() {
        }

        public Builder destination(String destination) {
            this.destination = destination;
            return this;
        }

        public Builder servers(String servers) {
            this.servers = servers;
            return this;
        }

        public Builder topics(List<String> topics) {
            this.topics = topics;
            return this;
        }

        public Builder dynamicTopic(Map<String, String> dynamicTopic) {
            this.dynamicTopic = dynamicTopic;
            return this;
        }

        public Builder groupId(String groupId) {
            this.groupId = groupId;
            return this;
        }

        public Builder filter(String filter) {
            this.filter = filter;
            return this;
        }

        public Builder batchSize(Integer batchSize) {
            this.batchSize = batchSize;
            return this;
        }

        public Builder timeout(Long timeout) {
            this.timeout = timeout;
            return this;
        }

        public Builder unit(TimeUnit unit) {
            this.unit = unit;
            return this;
        }

        public Builder flatMessage(Boolean flatMessage) {
            this.flatMessage = flatMessage;
            return this;
        }

        public Builder messageHandler(IMessageHandler<?> messageHandler) {
            this.messageHandler = messageHandler;
            return this;
        }

        public KafkaCanalClient build() {
            CanalMQConnector connector = new DynamicTopicKafkaCanalConnector(servers, dynamicTopic, topics, groupId, batchSize, flatMessage);
            KafkaCanalClient kafkaCanalClient = new KafkaCanalClient();
            kafkaCanalClient.messageHandler = messageHandler;
            kafkaCanalClient.connector = connector;
            kafkaCanalClient.destination = destination;
            kafkaCanalClient.filter = this.filter;
            kafkaCanalClient.unit = this.unit;
            kafkaCanalClient.batchSize = this.batchSize;
            kafkaCanalClient.timeout = this.timeout;
            kafkaCanalClient.flatMessage = this.flatMessage;
            return kafkaCanalClient;
        }
    }

}
