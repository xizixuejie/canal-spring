package io.xzxj.canal.core.handler.impl;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import io.xzxj.canal.core.context.EntryListenerContext;
import io.xzxj.canal.core.handler.AbstractMessageHandler;
import io.xzxj.canal.core.handler.RowDataHandler;

import java.util.concurrent.ExecutorService;

/**
 * @author xzxj
 * @date 2023/3/15 10:37
 */
public class AsyncMessageHandlerImpl extends AbstractMessageHandler {


    private final ExecutorService executor;

    public AsyncMessageHandlerImpl(EntryListenerContext entryListenerContext, RowDataHandler<CanalEntry.RowData> rowDataHandler, ExecutorService executor) {
        super(entryListenerContext, rowDataHandler);
        this.executor = executor;
    }

    @Override
    public void handleMessage(String destination, Message message) {
        executor.execute(() -> super.handleMessage(destination, message));
    }

}
