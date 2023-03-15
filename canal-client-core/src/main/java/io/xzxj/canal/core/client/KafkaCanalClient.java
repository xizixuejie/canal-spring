package io.xzxj.canal.core.client;

import com.alibaba.otter.canal.client.kafka.KafkaCanalConnector;
import com.alibaba.otter.canal.protocol.FlatMessage;
import io.xzxj.canal.core.handler.IMessageHandler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author xzxj
 * @date 2023/3/15 13:36
 */
public class KafkaCanalClient extends AbstractCanalClient {

    private static final Logger log = LoggerFactory.getLogger(KafkaCanalClient.class);

    @Override
    public void subscribe() {
        connector.subscribe();
    }

    @Override
    public void handleListening() {
        KafkaCanalConnector kafkaCanalConnector = (KafkaCanalConnector) connector;
        try {
            while (runStatus) {
                List<FlatMessage> messageList = kafkaCanalConnector.getFlatListWithoutAck(timeout, unit);
                log.debug("receive message={}", messageList);
                for (FlatMessage message : messageList) {
                    messageHandler.handleMessage(message);
                }
                kafkaCanalConnector.ack();
            }
        } catch (Exception e) {
            log.error("canal client exception", e);
        }
    }


    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String filter = StringUtils.EMPTY;
        private Integer batchSize = 1;
        private Long timeout = 1L;
        private TimeUnit unit = TimeUnit.SECONDS;
        private String servers;
        private String topic;
        private Integer partition;
        private String groupId;
        private IMessageHandler<?> messageHandler;

        private Builder() {
        }

        public Builder servers(String servers) {
            this.servers = servers;
            return this;
        }

        public Builder topic(String topic) {
            this.topic = topic;
            return this;
        }

        public Builder partition(Integer partition) {
            this.partition = partition;
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

        public Builder messageHandler(IMessageHandler<?> messageHandler) {
            this.messageHandler = messageHandler;
            return this;
        }

        public KafkaCanalClient build() {
            KafkaCanalConnector connector = new KafkaCanalConnector(servers, topic, partition, groupId, batchSize, true);
            KafkaCanalClient kafkaCanalClient = new KafkaCanalClient();
            kafkaCanalClient.messageHandler = messageHandler;
            kafkaCanalClient.connector = connector;
            kafkaCanalClient.filter = this.filter;
            kafkaCanalClient.unit = this.unit;
            kafkaCanalClient.batchSize = this.batchSize;
            kafkaCanalClient.timeout = this.timeout;
            return kafkaCanalClient;
        }
    }

}
