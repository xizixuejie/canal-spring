package io.xzxj.canal.core.handler.impl;

import com.alibaba.otter.canal.protocol.CanalEntry;
import io.xzxj.canal.core.handler.AbstractMessageHandler;
import io.xzxj.canal.core.handler.RowDataHandler;
import io.xzxj.canal.core.listener.EntryListener;

import java.util.List;

/**
 * @author xzxj
 * @date 2023/3/15 10:36
 */
public class SyncMessageHandlerImpl extends AbstractMessageHandler {

    public SyncMessageHandlerImpl(List<EntryListener<?>> entryListenerList, RowDataHandler<CanalEntry.RowData> rowDataHandler) {
        super(entryListenerList, rowDataHandler);
    }

}
