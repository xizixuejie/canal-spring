package io.xzxj.canal.spring.autoconfigure;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import io.xzxj.canal.core.client.AbstractCanalClient;
import io.xzxj.canal.core.client.TcpCanalClient;
import io.xzxj.canal.core.context.EntryListenerContext;
import io.xzxj.canal.core.factory.EntryColumnConvertFactory;
import io.xzxj.canal.core.handler.IMessageHandler;
import io.xzxj.canal.core.handler.RowDataHandler;
import io.xzxj.canal.core.handler.impl.AsyncMessageHandlerImpl;
import io.xzxj.canal.core.handler.impl.RowDataHandlerImpl;
import io.xzxj.canal.core.handler.impl.SyncMessageHandlerImpl;
import io.xzxj.canal.core.metadata.AbstractEntityInfoHelper;
import io.xzxj.canal.spring.client.CanalClientList;
import io.xzxj.canal.spring.properties.CanalProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.concurrent.ExecutorService;

/**
 * @author xzxj
 * @date 2023/3/11 11:37
 */
@EnableConfigurationProperties(CanalProperties.class)
@ConditionalOnProperty(value = "canal.server-mode", havingValue = "tcp", matchIfMissing = true)
@Import({ThreadPoolAutoConfiguration.class, CanalAutoConfiguration.class})
public class TcpClientAutoConfiguration {

    private final CanalProperties canalProperties;

    public TcpClientAutoConfiguration(CanalProperties canalProperties) {
        this.canalProperties = canalProperties;
    }

    @Bean
    @ConditionalOnMissingBean(RowDataHandler.class)
    public RowDataHandler<CanalEntry.RowData> rowDataHandler(AbstractEntityInfoHelper entityInfoHelper) {
        return new RowDataHandlerImpl(new EntryColumnConvertFactory(entityInfoHelper));
    }

    @Bean
    @ConditionalOnProperty(value = "canal.async", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean(IMessageHandler.class)
    public IMessageHandler<Message> asyncMessageHandler(EntryListenerContext entryListenerContext,
                                                        RowDataHandler<CanalEntry.RowData> rowDataHandler,
                                                        ExecutorService executorService) {
        return new AsyncMessageHandlerImpl(entryListenerContext, rowDataHandler, executorService);
    }

    @Bean
    @ConditionalOnProperty(value = "canal.async", havingValue = "false")
    @ConditionalOnMissingBean(IMessageHandler.class)
    public IMessageHandler<Message> syncMessageHandler(EntryListenerContext entryListenerContext,
                                                       RowDataHandler<CanalEntry.RowData> rowDataHandler) {
        return new SyncMessageHandlerImpl(entryListenerContext, rowDataHandler);
    }

    @Bean(initMethod = "init", destroyMethod = "destroy")
    @ConditionalOnMissingBean(AbstractCanalClient.class)
    public CanalClientList tcpCanalClientList(IMessageHandler<Message> messageHandler) {
        CanalClientList list = new CanalClientList();
        String[] destinations = canalProperties.getDestination().split(",");
        String server = canalProperties.getServer();
        String[] array = server.split(":");
        TcpCanalClient.Builder builder = TcpCanalClient.builder()
                .hostname(array[0])
                .port(Integer.parseInt(array[1]))
                .username(canalProperties.getUsername())
                .password(canalProperties.getPassword())
                .messageHandler(messageHandler)
                .batchSize(canalProperties.getBatchSize())
                .filter(canalProperties.getFilter())
                .timeout(canalProperties.getTimeout())
                .unit(canalProperties.getUnit());
        for (String destination : destinations) {
            list.add(builder.destination(destination).build());
        }
        return list;
    }

    @Bean(initMethod = "init", destroyMethod = "destroy")
    @ConditionalOnMissingBean({AbstractCanalClient.class, CanalClientList.class})
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
