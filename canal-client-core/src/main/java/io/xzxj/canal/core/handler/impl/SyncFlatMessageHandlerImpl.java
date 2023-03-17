package io.xzxj.canal.core.handler.impl;

import io.xzxj.canal.core.handler.AbstractFlatMessageHandler;
import io.xzxj.canal.core.handler.RowDataHandler;
import io.xzxj.canal.core.listener.EntryListener;

import java.util.List;
import java.util.Map;

/**
 * @author xzxj
 * @date 2023/3/15 13:48
 */
public class SyncFlatMessageHandlerImpl extends AbstractFlatMessageHandler {

    public SyncFlatMessageHandlerImpl(List<EntryListener<?>> entryListenerList, RowDataHandler<List<Map<String, String>>> rowDataHandler) {
        super(entryListenerList, rowDataHandler);
    }

}

