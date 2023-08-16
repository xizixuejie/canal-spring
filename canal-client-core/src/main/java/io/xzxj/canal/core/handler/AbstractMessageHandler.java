package io.xzxj.canal.core.handler;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import io.xzxj.canal.core.listener.EntryListener;
import io.xzxj.canal.core.util.EntryListenerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author xzxj
 * @date 2023/3/11 11:25
 */
public abstract class AbstractMessageHandler implements IMessageHandler<Message> {

    private static final Logger log = LoggerFactory.getLogger(AbstractMessageHandler.class);

    private final List<EntryListener<?>> entryListenerList;
    private final RowDataHandler<CanalEntry.RowData> rowDataHandler;

    public AbstractMessageHandler(List<EntryListener<?>> entryListenerList, RowDataHandler<CanalEntry.RowData> rowDataHandler) {
        this.entryListenerList = entryListenerList;
        this.rowDataHandler = rowDataHandler;
    }

    @Override
    public void handleMessage(Message message) {
        List<CanalEntry.Entry> entries = message.getEntries();
        for (CanalEntry.Entry entry : entries) {
            if (!CanalEntry.EntryType.ROWDATA.equals(entry.getEntryType())) {
                continue;
            }
            String schemaName = entry.getHeader().getSchemaName();
            String tableName = entry.getHeader().getTableName();

            EntryListener<?> entryListener = EntryListenerUtil.findEntryListener(entryListenerList, schemaName, tableName);

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
                    if (entryListener != null) {
                        rowDataHandler.handleRowData(rowData, entryListener, eventType);
                    }
                }
            } catch (Exception e) {
                log.error("handle row data error", e);
            }
        }
    }

}
