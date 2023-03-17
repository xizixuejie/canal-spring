package io.xzxj.canal.core.handler.impl;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import io.xzxj.canal.core.handler.AbstractMessageHandler;
import io.xzxj.canal.core.handler.RowDataHandler;
import io.xzxj.canal.core.listener.EntryListener;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * @author xzxj
 * @date 2023/3/15 10:37
 */
public class AsyncMessageHandlerImpl extends AbstractMessageHandler {


    private final ExecutorService executor;

    public AsyncMessageHandlerImpl(List<EntryListener<?>> entryListenerList, RowDataHandler<CanalEntry.RowData> rowDataHandler, ExecutorService executor) {
        super(entryListenerList, rowDataHandler);
        this.executor = executor;
    }

    @Override
    public void handleMessage(Message message) {
        executor.execute(() -> super.handleMessage(message));
    }

}
