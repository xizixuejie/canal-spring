package io.xzxj.canal.core.handler.impl;

import io.xzxj.canal.core.context.EntryListenerContext;
import io.xzxj.canal.core.handler.AbstractFlatMessageHandler;
import io.xzxj.canal.core.handler.RowDataHandler;

import java.util.List;
import java.util.Map;

/**
 * @author xzxj
 * @date 2023/3/15 13:48
 */
public class SyncFlatMessageHandlerImpl extends AbstractFlatMessageHandler {

    public SyncFlatMessageHandlerImpl(EntryListenerContext entryListenerContext, RowDataHandler<List<Map<String, String>>> rowDataHandler) {
        super(entryListenerContext, rowDataHandler);
    }

}

