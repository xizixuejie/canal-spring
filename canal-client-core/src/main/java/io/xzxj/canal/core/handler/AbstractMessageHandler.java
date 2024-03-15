package io.xzxj.canal.core.handler;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import io.xzxj.canal.core.context.EntryListenerContext;
import io.xzxj.canal.core.listener.EntryListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author xzxj
 * @date 2023/3/11 11:25
 */
public abstract class AbstractMessageHandler implements IMessageHandler<Message> {

    private static final Logger log = LoggerFactory.getLogger(AbstractMessageHandler.class);

    private final EntryListenerContext entryListenerContext;
    private final RowDataHandler<CanalEntry.RowData> rowDataHandler;

    public AbstractMessageHandler(EntryListenerContext entryListenerContext, RowDataHandler<CanalEntry.RowData> rowDataHandler) {
        this.entryListenerContext = entryListenerContext;
        this.rowDataHandler = rowDataHandler;
    }

    @Override
    public void handleMessage(String destination, Message message) {
        List<CanalEntry.Entry> entries = message.getEntries();
        for (CanalEntry.Entry entry : entries) {
            String schemaName = entry.getHeader().getSchemaName();
            String tableName = entry.getHeader().getTableName();
            List<EntryListener<?>> entryListenerList = entryListenerContext.findEntryListener(message.getId(), destination, schemaName, tableName);
            if (!CanalEntry.EntryType.ROWDATA.equals(entry.getEntryType()) || entryListenerList.isEmpty()) {
                continue;
            }

            CanalEntry.RowChange rowChange;
            try {
                rowChange = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
            } catch (Exception e) {
                throw new RuntimeException("parse event has an error , data:" + entry, e);
            }
            List<CanalEntry.RowData> rowDataList = rowChange.getRowDatasList();
            CanalEntry.EventType eventType = rowChange.getEventType();
            try {
                for (CanalEntry.RowData rowData : rowDataList) {
                    for (EntryListener<?> listener : entryListenerList) {
                        rowDataHandler.handleRowData(rowData, listener, eventType);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("handle row data error,", e);
            }
        }
    }

}
