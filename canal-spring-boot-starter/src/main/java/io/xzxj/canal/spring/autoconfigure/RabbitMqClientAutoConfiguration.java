package io.xzxj.canal.spring.autoconfigure;

import com.alibaba.otter.canal.protocol.FlatMessage;
import io.xzxj.canal.core.client.RabbitMqCanalClient;
import io.xzxj.canal.core.factory.MapConvertFactory;
import io.xzxj.canal.core.handler.IMessageHandler;
import io.xzxj.canal.core.handler.RowDataHandler;
import io.xzxj.canal.core.handler.impl.AsyncFlatMessageHandlerImpl;
import io.xzxj.canal.core.handler.impl.MapRowDataHandlerImpl;
import io.xzxj.canal.core.handler.impl.SyncFlatMessageHandlerImpl;
import io.xzxj.canal.core.listener.EntryListener;
import io.xzxj.canal.spring.properties.CanalProperties;
import io.xzxj.canal.spring.properties.CanalRabbitMqProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * @author xzxj
 * @date 2023/3/24 9:49
 */
@EnableConfigurationProperties(CanalProperties.class)
@ConditionalOnProperty(value = "canal.server-mode", havingValue = "rabbit_mq")
@Import(ThreadPoolAutoConfiguration.class)
public class RabbitMqClientAutoConfiguration {

    private final CanalProperties canalProperties;

    public RabbitMqClientAutoConfiguration(CanalProperties canalProperties) {
        this.canalProperties = canalProperties;
    }

    @Bean
    public RowDataHandler<List<Map<String, String>>> rowDataHandler() {
        return new MapRowDataHandlerImpl(new MapConvertFactory());
    }

    @Bean
    @ConditionalOnProperty(value = "canal.async", havingValue = "true", matchIfMissing = true)
    public IMessageHandler<FlatMessage> asyncFlatMessageHandler(RowDataHandler<List<Map<String, String>>> rowDataHandler,
                                                                List<EntryListener<?>> entryListenerList,
                                                                ExecutorService executorService) {
        return new AsyncFlatMessageHandlerImpl(entryListenerList, rowDataHandler, executorService);
    }

    @Bean
    @ConditionalOnProperty(value = "canal.async", havingValue = "false")
    public IMessageHandler<FlatMessage> syncFlatMessageHandler(RowDataHandler<List<Map<String, String>>> rowDataHandler,
                                                               List<EntryListener<?>> entryListenerList) {
        return new SyncFlatMessageHandlerImpl(entryListenerList, rowDataHandler);
    }

    @Bean(initMethod = "init", destroyMethod = "destroy")
    public RabbitMqCanalClient rabbitMqCanalClient(IMessageHandler<FlatMessage> messageHandler) {
        CanalRabbitMqProperties mqProperties = canalProperties.getRabbitMq();
        return RabbitMqCanalClient.builder().servers(canalProperties.getServer())
                .virtualHost(mqProperties.getVirtualHost())
                .queueName(canalProperties.getDestination())
                .accessKey(mqProperties.getAccessKey())
                .secretKey(mqProperties.getSecretKey())
                .resourceOwnerId(mqProperties.getResourceOwnerId())
                .username(mqProperties.getUsername())
                .password(mqProperties.getPassword())
                .messageHandler(messageHandler)
                .batchSize(canalProperties.getBatchSize())
                .filter(canalProperties.getFilter())
                .timeout(canalProperties.getTimeout())
                .unit(canalProperties.getUnit())
                .build();
    }

}
