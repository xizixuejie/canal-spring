package io.xzxj.canal.core.handler;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import io.xzxj.canal.core.listener.EntryListener;
import io.xzxj.canal.core.util.MapValueUtil;
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
public abstract class AbstractMessageHandler implements IMessageHandler<Message> {

    private static final Logger log = LoggerFactory.getLogger(AbstractMessageHandler.class);

    private final Map<String, EntryListener<?>> entryListenerMap;
    private final RowDataHandler<CanalEntry.RowData> rowDataHandler;

    public AbstractMessageHandler(List<EntryListener<?>> entryListenerList, RowDataHandler<CanalEntry.RowData> rowDataHandler) {
        this.entryListenerMap = entryListenerList.stream()
                .collect(Collectors.toMap(TableInfoUtil::getTableName, v -> v, (v1, v2) -> v1));
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

            // 先带数据库名字找EntryListener
            EntryListener<?> entryListener = entryListenerMap.get(schemaName + "." + tableName);
            if (entryListener == null) {
                // 如果没有找到 只用表名找EntryListener
                entryListener = entryListenerMap.get(tableName);
            }

            if (entryListener == null) {
                // 如果没有找到  用正则表达式找EntryListener
                entryListener = MapValueUtil.getValueByRegex(entryListenerMap, tableName);
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
