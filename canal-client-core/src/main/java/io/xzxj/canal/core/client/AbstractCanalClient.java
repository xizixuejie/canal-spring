package io.xzxj.canal.core.client;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.protocol.exception.CanalClientException;
import io.xzxj.canal.core.handler.IMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author xzxj
 * @date 2023/3/11 10:33
 */
public abstract class AbstractCanalClient implements ICanalClient {

    private static final Logger log = LoggerFactory.getLogger(AbstractCanalClient.class);

    protected volatile boolean runStatus;

    private Thread thread;

    protected CanalConnector connector;

    protected String destination;

    protected IMessageHandler messageHandler;

    protected String filter = "";

    protected Integer batchSize = 1;

    protected Long timeout = 1L;

    protected TimeUnit unit = TimeUnit.SECONDS;

    @Override
    public void init() {
        log.info("canal client init");
        this.connectCanal();
        thread = new Thread(this::handleListening);
        thread.setName("canal-client-thread");
        runStatus = true;
        thread.start();
    }

    private void connectCanal() {
        try {
            log.info("canal client connecting");
            connector.connect();
            this.subscribe();
            log.info("canal client connect success");
        } catch (CanalClientException e) {
            log.error("canal client connect error: {}", e.getMessage(), e);
            this.destroy();
        }
    }

    public void subscribe() {
        connector.subscribe(filter);
    }

    @Override
    public void destroy() {
        connector.unsubscribe();
        log.info("canal client destroy");
        if (thread != null) {
            thread.interrupt();
        }
        runStatus = false;
    }

}
