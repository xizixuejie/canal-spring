package io.xzxj.canal.core.client;

import com.alibaba.otter.canal.client.rabbitmq.RabbitMQCanalConnector;
import com.alibaba.otter.canal.protocol.FlatMessage;
import io.xzxj.canal.core.handler.IMessageHandler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author xzxj
 * @date 2023/3/24 9:23
 */
public class RabbitMqCanalClient extends AbstractCanalClient {

    private static final Logger log = LoggerFactory.getLogger(RabbitMqCanalClient.class);

    @Override
    public void handleListening() {
        RabbitMQCanalConnector kafkaCanalConnector = (RabbitMQCanalConnector) connector;
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

    public static class Builder {

        // 链接地址
        private String servers;

        // 主机名
        private String virtualHost;

        private String queueName;

        // 一些鉴权信息
        private String accessKey;
        private String secretKey;
        private Long resourceOwnerId;
        private String username;
        private String password;

        private String filter = StringUtils.EMPTY;

        private Long timeout = 1L;

        private TimeUnit unit = TimeUnit.SECONDS;

        private Integer batchSize = 1;

        private IMessageHandler<?> messageHandler;

        private Builder() {
        }

        public Builder servers(String servers) {
            this.servers = servers;
            return this;
        }

        public Builder virtualHost(String vhost) {
            this.virtualHost = vhost;
            return this;
        }

        public Builder queueName(String queueName) {
            this.queueName = queueName;
            return this;
        }

        public Builder accessKey(String accessKey) {
            this.accessKey = accessKey;
            return this;
        }

        public Builder secretKey(String secretKey) {
            this.secretKey = secretKey;
            return this;
        }

        public Builder resourceOwnerId(Long resourceOwnerId) {
            this.resourceOwnerId = resourceOwnerId;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder filter(String filter) {
            this.filter = filter;
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

        public Builder batchSize(Integer batchSize) {
            this.batchSize = batchSize;
            return this;
        }

        public Builder messageHandler(IMessageHandler<?> messageHandler) {
            this.messageHandler = messageHandler;
            return this;
        }

        public RabbitMqCanalClient build() {
            RabbitMQCanalConnector connector = new RabbitMQCanalConnector(servers, virtualHost, queueName,
                    accessKey, secretKey, username, password,
                    resourceOwnerId, true);
            RabbitMqCanalClient rabbitMqCanalClient = new RabbitMqCanalClient();
            rabbitMqCanalClient.messageHandler = messageHandler;
            rabbitMqCanalClient.connector = connector;
            rabbitMqCanalClient.filter = this.filter;
            rabbitMqCanalClient.unit = this.unit;
            rabbitMqCanalClient.batchSize = this.batchSize;
            rabbitMqCanalClient.timeout = this.timeout;
            return rabbitMqCanalClient;
        }

    }

}
