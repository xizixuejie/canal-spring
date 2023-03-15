package io.xzxj.canal.spring.autoconfigure;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import io.xzxj.canal.core.client.TcpCanalClient;
import io.xzxj.canal.core.factory.EntryColumnConvertFactory;
import io.xzxj.canal.core.handler.IMessageHandler;
import io.xzxj.canal.core.handler.RowDataHandler;
import io.xzxj.canal.core.handler.impl.AsyncMessageHandlerImpl;
import io.xzxj.canal.core.handler.impl.RowDataHandlerImpl;
import io.xzxj.canal.core.handler.impl.SyncMessageHandlerImpl;
import io.xzxj.canal.core.listener.EntryListener;
import io.xzxj.canal.spring.properties.CanalProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * @author xzxj
 * @date 2023/3/11 11:37
 */
@Configuration
@EnableConfigurationProperties(CanalProperties.class)
@ConditionalOnProperty(value = "canal.server-mode", havingValue = "tcp")
@Import(ThreadPoolAutoConfiguration.class)
public class TcpClientAutoConfiguration {

    private final CanalProperties canalProperties;

    public TcpClientAutoConfiguration(CanalProperties canalProperties) {
        this.canalProperties = canalProperties;
    }

    @Bean
    @ConditionalOnProperty(value = "canal.async", havingValue = "true", matchIfMissing = true)
    public IMessageHandler<Message> asyncMessageHandler(List<EntryListener<?>> entryListenerList,
                                                        RowDataHandler<CanalEntry.RowData> rowDataHandler,
                                                        ExecutorService executorService) {
        return new AsyncMessageHandlerImpl(entryListenerList, rowDataHandler, executorService);
    }

    @Bean
    @ConditionalOnProperty(value = "canal.async", havingValue = "false")
    public IMessageHandler<Message> syncMessageHandler(List<EntryListener<?>> entryListenerList,
                                                       RowDataHandler<CanalEntry.RowData> rowDataHandler) {
        return new SyncMessageHandlerImpl(entryListenerList, rowDataHandler);
    }

    @Bean
    public RowDataHandler<CanalEntry.RowData> rowDataHandler() {
        return new RowDataHandlerImpl(new EntryColumnConvertFactory());
    }

    @Bean(initMethod = "init", destroyMethod = "destroy")
    public TcpCanalClient tcpCanalClient(IMessageHandler<Message> messageHandler) {
        String server = canalProperties.getServer();
        String[] array = server.split(":");
        return TcpCanalClient.builder()
                .hostname(array[0])
                .port(Integer.parseInt(array[1]))
                .destination(canalProperties.getDestination())
                .username(canalProperties.getUsername())
                .password(canalProperties.getPassword())
                .messageHandler(messageHandler)
                .batchSize(canalProperties.getBatchSize())
                .filter(canalProperties.getFilter())
                .timeout(canalProperties.getTimeout())
                .unit(canalProperties.getUnit())
                .build();
    }

}
