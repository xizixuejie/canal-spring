package io.xzxj.canal.core.handler;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import io.xzxj.canal.core.listener.EntryListener;
import io.xzxj.canal.core.util.TableInfoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author xzxj
 * @date 2023/3/11 11:25
 */
public class AbstractMessageHandler implements IMessageHandler<Message> {

    private static final Logger log = LoggerFactory.getLogger(AbstractMessageHandler.class);

    private Map<String, EntryListener<?>> entryListenerMap;
    private RowDataHandler<CanalEntry.RowData> rowDataHandler;

    public AbstractMessageHandler(List<EntryListener<?>> entryListenerList, RowDataHandler<CanalEntry.RowData> rowDataHandler) {
        this.entryListenerMap = entryListenerList.stream()
                .collect(Collectors.toMap(TableInfoUtil::getTableName, v -> v, (v1, v2) -> v1));
        this.rowDataHandler = rowDataHandler;
    }


    @Override
    public void handleMessage(Message message) throws Exception {
        List<CanalEntry.Entry> entries = message.getEntries();
        for (CanalEntry.Entry entry : entries) {
            if (!CanalEntry.EntryType.ROWDATA.equals(entry.getEntryType())) {
                continue;
            }
            EntryListener<?> entryListener = entryListenerMap.get(entry.getHeader().getTableName());
            CanalEntry.RowChange rowChange;
            try {
                rowChange = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
            } catch (Exception e) {
                throw new RuntimeException("parse event has an error , data:" + entry, e);
            }
            List<CanalEntry.RowData> rowDataList = rowChange.getRowDatasList();
            CanalEntry.EventType eventType = rowChange.getEventType();
            for (CanalEntry.RowData rowData : rowDataList) {
                rowDataHandler.handleRowData(rowData, entryListener, eventType);
            }

        }
    }

}
