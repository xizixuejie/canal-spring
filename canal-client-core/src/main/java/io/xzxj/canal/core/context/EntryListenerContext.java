package io.xzxj.canal.core.context;

import io.xzxj.canal.core.annotation.CanalListener;
import io.xzxj.canal.core.listener.EntryListener;
import io.xzxj.canal.core.metadata.AbstractEntityInfoHelper;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public final class EntryListenerContext {

    private final DatabaseListenerContext databaseListenerContext = DatabaseListenerContext.getInstance();

    private final AbstractEntityInfoHelper entityInfoHelper;

    public EntryListenerContext(AbstractEntityInfoHelper entityInfoHelper,
                                List<EntryListener<?>> entryListenerList) {
        this.entityInfoHelper = entityInfoHelper;
        this.initEntryListenerMap(entryListenerList);
    }

    public List<EntryListener<?>> findEntryListener(String schemaName, String tableName) {
        return databaseListenerContext.getListenersByRegex(schemaName, tableName);
    }

    private void initEntryListenerMap(List<EntryListener<?>> entryListenerList) {
        for (EntryListener<?> entryListener : entryListenerList) {
            CanalListener annotation = entryListener.getClass().getAnnotation(CanalListener.class);
            if (annotation == null) {
                continue;
            }
            String[] schemaNames = annotation.schemaName();
            String tableName = annotation.tableName();
            if (StringUtils.isBlank(tableName)) {
                tableName = entityInfoHelper.getTableName(entryListener);
            }
            if (schemaNames.length == 0) {
                databaseListenerContext.put(tableName, entryListener);
                continue;
            }
            for (String schemaName : schemaNames) {
                databaseListenerContext.put(schemaName, tableName, entryListener);
            }
        }
    }

}
