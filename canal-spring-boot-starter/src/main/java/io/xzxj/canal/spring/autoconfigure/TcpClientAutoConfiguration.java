package io.xzxj.canal.spring.autoconfigure;

import com.alibaba.otter.canal.protocol.CanalEntry;
import io.xzxj.canal.core.client.TcpCanalClient;
import io.xzxj.canal.core.factory.EntryColumnModelFactory;
import io.xzxj.canal.core.handler.AbstractMessageHandler;
import io.xzxj.canal.core.handler.IMessageHandler;
import io.xzxj.canal.core.handler.RowDataHandler;
import io.xzxj.canal.core.handler.impl.RowDataHandlerImpl;
import io.xzxj.canal.core.listener.EntryListener;
import io.xzxj.canal.spring.properties.CanalProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * @author xzxj
 * @date 2023/3/11 11:37
 */
@EnableConfigurationProperties(CanalProperties.class)
@ConditionalOnProperty(value = "canal.server-mode", havingValue = "tcp", matchIfMissing = true)
public class TcpClientAutoConfiguration {

    private final CanalProperties canalProperties;

    public TcpClientAutoConfiguration(CanalProperties canalProperties) {
        this.canalProperties = canalProperties;
    }

    @Bean
    public IMessageHandler messageHandler(List<EntryListener<?>>entryListenerList,
                                          RowDataHandler<CanalEntry.RowData> rowDataHandler) {
        return new AbstractMessageHandler(entryListenerList, rowDataHandler);
    }

    @Bean
    public RowDataHandler<CanalEntry.RowData> rowDataHandler() {
        return new RowDataHandlerImpl(new EntryColumnModelFactory());
    }

    @Bean(initMethod = "init", destroyMethod = "destroy")
    public TcpCanalClient tcpCanalClient(IMessageHandler messageHandler) {
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
