package io.xzxj.canal.core.handler.impl;

import com.alibaba.otter.canal.protocol.CanalEntry;
import io.xzxj.canal.core.context.EntryListenerContext;
import io.xzxj.canal.core.handler.AbstractMessageHandler;
import io.xzxj.canal.core.handler.RowDataHandler;

/**
 * @author xzxj
 * @date 2023/3/15 10:36
 */
public class SyncMessageHandlerImpl extends AbstractMessageHandler {

    public SyncMessageHandlerImpl(EntryListenerContext entryListenerContext, RowDataHandler<CanalEntry.RowData> rowDataHandler) {
        super(entryListenerContext, rowDataHandler);
    }

}
