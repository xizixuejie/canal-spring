package io.xzxj.canal.spring.autoconfigure;

import com.alibaba.otter.canal.protocol.FlatMessage;
import io.xzxj.canal.core.client.AbstractCanalClient;
import io.xzxj.canal.core.client.KafkaCanalClient;
import io.xzxj.canal.core.factory.MapConvertFactory;
import io.xzxj.canal.core.handler.IMessageHandler;
import io.xzxj.canal.core.handler.RowDataHandler;
import io.xzxj.canal.core.handler.impl.AsyncFlatMessageHandlerImpl;
import io.xzxj.canal.core.handler.impl.MapRowDataHandlerImpl;
import io.xzxj.canal.core.handler.impl.SyncFlatMessageHandlerImpl;
import io.xzxj.canal.core.listener.EntryListener;
import io.xzxj.canal.spring.properties.CanalProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * @author xzxj
 * @date 2023/3/15 13:51
 */
@EnableConfigurationProperties(CanalProperties.class)
@ConditionalOnProperty(value = "canal.server-mode", havingValue = "kafka")
@Import(ThreadPoolAutoConfiguration.class)
public class KafkaClientAutoConfiguration {

    private final CanalProperties canalProperties;

    public KafkaClientAutoConfiguration(CanalProperties canalProperties) {
        this.canalProperties = canalProperties;
    }

    @Bean
    @ConditionalOnMissingBean(RowDataHandler.class)
    public RowDataHandler<List<Map<String, String>>> rowDataHandler() {
        return new MapRowDataHandlerImpl(new MapConvertFactory());
    }

    @Bean
    @ConditionalOnProperty(value = "canal.async", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean(IMessageHandler.class)
    public IMessageHandler<FlatMessage> asyncFlatMessageHandler(RowDataHandler<List<Map<String, String>>> rowDataHandler,
                                                                List<EntryListener<?>> entryHandlers,
                                                                ExecutorService executorService) {
        return new AsyncFlatMessageHandlerImpl(entryHandlers, rowDataHandler, executorService);
    }

    @Bean
    @ConditionalOnProperty(value = "canal.async", havingValue = "false")
    @ConditionalOnMissingBean(IMessageHandler.class)
    public IMessageHandler<FlatMessage> syncFlatMessageHandler(RowDataHandler<List<Map<String, String>>> rowDataHandler,
                                                               List<EntryListener<?>> entryHandlers) {
        return new SyncFlatMessageHandlerImpl(entryHandlers, rowDataHandler);
    }

    @Bean(initMethod = "init", destroyMethod = "destroy")
    @ConditionalOnMissingBean(AbstractCanalClient.class)
    public KafkaCanalClient kafkaCanalClient(IMessageHandler<FlatMessage> messageHandler) {
        return KafkaCanalClient.builder().servers(canalProperties.getServer())
                .groupId(canalProperties.getKafka().getGroupId())
                .topic(canalProperties.getDestination())
                .partition(canalProperties.getKafka().getPartition())
                .messageHandler(messageHandler)
                .batchSize(canalProperties.getBatchSize())
                .filter(canalProperties.getFilter())
                .timeout(canalProperties.getTimeout())
                .unit(canalProperties.getUnit())
                .build();
    }

}
