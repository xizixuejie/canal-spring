package io.xzxj.canal.core.client;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.Message;
import io.xzxj.canal.core.handler.IMessageHandler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * @author xzxj
 * @date 2023/3/11 10:51
 */
public class TcpCanalClient extends AbstractCanalClient {

    private static final Logger log = LoggerFactory.getLogger(TcpCanalClient.class);

    @Override
    public void handleListening() {
        try {
            while (runStatus) {
                Message message = connector.getWithoutAck(batchSize, timeout, unit);
                log.debug("receive message={}", message);
                long batchId = message.getId();
                if (message.getId() != -1 && message.getEntries().size() != 0) {
                    messageHandler.handleMessage(message);
                }
                connector.ack(batchId);
            }
        } catch (Exception e) {
            log.error("canal client exception", e);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String filter = StringUtils.EMPTY;
        private Integer batchSize = 1;
        private Long timeout = 1L;
        private TimeUnit unit = TimeUnit.SECONDS;
        private String hostname;
        private Integer port;
        private String destination;
        private String username;
        private String password;
        private IMessageHandler<?> messageHandler;

        private Builder() {
        }

        public Builder hostname(String hostname) {
            this.hostname = hostname;
            return this;
        }

        public Builder port(Integer port) {
            this.port = port;
            return this;
        }

        public Builder destination(String destination) {
            this.destination = destination;
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

        public TcpCanalClient build() {
            CanalConnector canalConnector = CanalConnectors.newSingleConnector(new InetSocketAddress(hostname, port), destination, username, password);
            TcpCanalClient tcpCanalClient = new TcpCanalClient();
            tcpCanalClient.connector = canalConnector;
            tcpCanalClient.messageHandler = messageHandler;
            tcpCanalClient.filter = this.filter;
            tcpCanalClient.unit = this.unit;
            tcpCanalClient.batchSize = this.batchSize;
            tcpCanalClient.timeout = this.timeout;
            return tcpCanalClient;
        }
    }

}
