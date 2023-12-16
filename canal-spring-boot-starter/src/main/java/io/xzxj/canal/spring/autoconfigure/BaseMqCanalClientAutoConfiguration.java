package io.xzxj.canal.spring.autoconfigure;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.FlatMessage;
import com.alibaba.otter.canal.protocol.Message;
import io.xzxj.canal.core.context.EntryListenerContext;
import io.xzxj.canal.core.factory.EntryColumnConvertFactory;
import io.xzxj.canal.core.factory.MapConvertFactory;
import io.xzxj.canal.core.handler.IMessageHandler;
import io.xzxj.canal.core.handler.RowDataHandler;
import io.xzxj.canal.core.handler.impl.AsyncFlatMessageHandlerImpl;
import io.xzxj.canal.core.handler.impl.AsyncMessageHandlerImpl;
import io.xzxj.canal.core.handler.impl.MapRowDataHandlerImpl;
import io.xzxj.canal.core.handler.impl.RowDataHandlerImpl;
import io.xzxj.canal.core.handler.impl.SyncFlatMessageHandlerImpl;
import io.xzxj.canal.core.handler.impl.SyncMessageHandlerImpl;
import io.xzxj.canal.core.metadata.AbstractEntityInfoHelper;
import io.xzxj.canal.spring.properties.CanalProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public class BaseMqCanalClientAutoConfiguration {

    protected final CanalProperties canalProperties;

    public BaseMqCanalClientAutoConfiguration(CanalProperties canalProperties) {
        this.canalProperties = canalProperties;
    }

    @Bean("rowDataHandler")
    @ConditionalOnMissingBean(RowDataHandler.class)
    @ConditionalOnProperty(value = "canal.mq.flat-message", havingValue = "false")
    public RowDataHandler<CanalEntry.RowData> messageRowDataHandler(AbstractEntityInfoHelper entityInfoHelper) {
        return new RowDataHandlerImpl(new EntryColumnConvertFactory(entityInfoHelper));
    }

    @Bean
    @ConditionalOnMissingBean(IMessageHandler.class)
    @ConditionalOnExpression("${canal.async:true} and ${canal.mq.flat-message:true} == false")
    public IMessageHandler<Message> asyncMessageHandler(RowDataHandler<CanalEntry.RowData> rowDataHandler,
                                                        EntryListenerContext entryListenerContext,
                                                        ExecutorService executorService) {
        return new AsyncMessageHandlerImpl(entryListenerContext, rowDataHandler, executorService);
    }

    @Bean
    @ConditionalOnMissingBean(IMessageHandler.class)
    @ConditionalOnExpression("${canal.async:true} == false and ${canal.mq.flat-message:true} == false")
    public IMessageHandler<Message> syncMessageHandler(RowDataHandler<CanalEntry.RowData> rowDataHandler,
                                                       EntryListenerContext entryListenerContext) {
        return new SyncMessageHandlerImpl(entryListenerContext, rowDataHandler);
    }

    @Bean("rowDataHandler")
    @ConditionalOnMissingBean(RowDataHandler.class)
    @ConditionalOnProperty(value = "canal.mq.flat-message", havingValue = "true", matchIfMissing = true)
    public RowDataHandler<List<Map<String, String>>> flatMessageRowDataHandler(AbstractEntityInfoHelper entityInfoHelper) {
        return new MapRowDataHandlerImpl(new MapConvertFactory(entityInfoHelper));
    }

    @Bean
    @ConditionalOnMissingBean(IMessageHandler.class)
    @ConditionalOnExpression("${canal.async:true} and ${canal.mq.flat-message:true}")
    public IMessageHandler<FlatMessage> asyncFlatMessageHandler(RowDataHandler<List<Map<String, String>>> rowDataHandler,
                                                                EntryListenerContext entryListenerContext,
                                                                ExecutorService executorService) {
        return new AsyncFlatMessageHandlerImpl(entryListenerContext, rowDataHandler, executorService);
    }

    @Bean
    @ConditionalOnMissingBean(IMessageHandler.class)
    @ConditionalOnExpression("${canal.async:true} == false and ${canal.mq.flat-message:true}")
    public IMessageHandler<FlatMessage> syncFlatMessageHandler(RowDataHandler<List<Map<String, String>>> rowDataHandler,
                                                               EntryListenerContext entryListenerContext) {
        return new SyncFlatMessageHandlerImpl(entryListenerContext, rowDataHandler);
    }

}
