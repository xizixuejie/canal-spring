package io.xzxj.canal.core.client;

import com.alibaba.fastjson2.JSONException;
import com.alibaba.otter.canal.client.CanalMQConnector;
import com.alibaba.otter.canal.protocol.FlatMessage;
import com.alibaba.otter.canal.protocol.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

import java.util.List;

/**
 * @author Hision
 * @date 2023/12/12 21:25
 */
public abstract class AbstractMqCanalClient extends AbstractCanalClient {
    private static final Logger log = LoggerFactory.getLogger(AbstractMqCanalClient.class);

    protected Boolean flatMessage = Boolean.TRUE;

    /**
     * 执行监听
     *
     * @param flatMessage 是否 JSON
     */
    void handleListening(@NonNull Boolean flatMessage) {
        while (runStatus) {
            if (Boolean.TRUE.equals(flatMessage)) {
                defaultFlatMessageHandle();
            } else {
                defaultMessageHandle();
            }
        }
        connector.unsubscribe();
        connector.disconnect();
    }

    protected final void defaultFlatMessageHandle() {
        CanalMQConnector mqConnector = (CanalMQConnector) connector;
        try {
            List<FlatMessage> flatMessages = mqConnector.getFlatListWithoutAck(timeout, unit);
            log.debug("receive message = {}", flatMessages);
            for (FlatMessage message : flatMessages) {
                messageHandler.handleMessage(message);
            }
            mqConnector.ack();
        } catch (JSONException e) {
            log.error("canal 消息json解析异常", e);
        } catch (Exception e) {
            log.error("canal 消费异常 回滚消息", e);
            connector.rollback();
        }
    }

    protected final void defaultMessageHandle() {
        CanalMQConnector mqConnector = (CanalMQConnector) connector;
        try {
            List<Message> messages = mqConnector.getListWithoutAck(timeout, unit);
            log.debug("receive message = {}", messages);
            for (Message message : messages) {
                messageHandler.handleMessage(message);
            }
            mqConnector.ack();
        } catch (JSONException e) {
            log.error("canal 消息json解析异常", e);
        } catch (Exception e) {
            log.error("canal 消费异常 回滚消息", e);
            connector.rollback();
        }
    }
}
