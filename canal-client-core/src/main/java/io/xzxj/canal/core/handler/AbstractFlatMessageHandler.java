package io.xzxj.canal.core.handler;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.FlatMessage;
import io.xzxj.canal.core.context.EntryListenerContext;
import io.xzxj.canal.core.listener.EntryListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author xzxj
 * @date 2023/3/15 13:42
 */
public abstract class AbstractFlatMessageHandler implements IMessageHandler<FlatMessage> {

    private static final Logger log = LoggerFactory.getLogger(AbstractFlatMessageHandler.class);

    private final EntryListenerContext entryListenerContext;
    private final RowDataHandler<List<Map<String, String>>> rowDataHandler;

    public AbstractFlatMessageHandler(EntryListenerContext entryListenerContext,
                                      RowDataHandler<List<Map<String, String>>> rowDataHandler) {
        this.entryListenerContext = entryListenerContext;
        this.rowDataHandler = rowDataHandler;
    }

    @Override
    public void handleMessage(FlatMessage flatMessage) {
        List<Map<String, String>> messageData = flatMessage.getData();
        if (messageData == null || messageData.isEmpty()) {
            return;
        }

        String schemaName = flatMessage.getDatabase();
        String tableName = flatMessage.getTable();
        List<EntryListener<?>> entryListenerList = entryListenerContext.findEntryListener(schemaName, tableName);
        if (entryListenerList.isEmpty()) {
            return;
        }

        for (int i = 0; i < messageData.size(); i++) {
            CanalEntry.EventType eventType = CanalEntry.EventType.valueOf(flatMessage.getType());
            List<Map<String, String>> maps;
            if (eventType.equals(CanalEntry.EventType.UPDATE)) {
                Map<String, String> map = messageData.get(i);
                Map<String, String> oldMap = flatMessage.getOld().get(i);
                maps = Stream.of(map, oldMap).collect(Collectors.toList());
            } else {
                maps = Stream.of(messageData.get(i)).collect(Collectors.toList());
            }
            try {
                for (EntryListener<?> listener : entryListenerList) {
                    rowDataHandler.handleRowData(maps, listener, eventType);
                }
            } catch (Exception e) {
                throw new RuntimeException("parse event has an error , data:" + messageData, e);
            }
        }
    }

}
