package io.xzxj.canal.core.handler.impl;

import com.alibaba.otter.canal.protocol.FlatMessage;
import io.xzxj.canal.core.handler.AbstractFlatMessageHandler;
import io.xzxj.canal.core.handler.RowDataHandler;
import io.xzxj.canal.core.listener.EntryListener;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * @author xzxj
 * @date 2023/3/15 13:49
 */
public class AsyncFlatMessageHandlerImpl extends AbstractFlatMessageHandler {


    private final ExecutorService executor;

    public AsyncFlatMessageHandlerImpl(List<EntryListener<?>> entryHandlers,
                                       RowDataHandler<List<Map<String, String>>> rowDataHandler,
                                       ExecutorService executor) {
        super(entryHandlers, rowDataHandler);
        this.executor = executor;
    }

    @Override
    public void handleMessage(FlatMessage flatMessage) {
        executor.execute(() -> super.handleMessage(flatMessage));
    }

}
