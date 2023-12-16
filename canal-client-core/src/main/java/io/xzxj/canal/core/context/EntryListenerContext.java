package io.xzxj.canal.core.context;

import io.xzxj.canal.core.annotation.CanalListener;
import io.xzxj.canal.core.listener.EntryListener;
import io.xzxj.canal.core.metadata.AbstractEntityInfoHelper;
import io.xzxj.canal.core.util.MapValueUtil;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class EntryListenerContext {

    public static final String DEFAULT_KEY = "DEFAULT";

    private static final Map<String, Map<String, EntryListener<?>>> ENTRY_LISTENER_MAP = new ConcurrentHashMap<>();

    private final AbstractEntityInfoHelper entityInfoHelper;

    public EntryListenerContext(AbstractEntityInfoHelper entityInfoHelper,
                                List<EntryListener<?>> entryListenerList) {
        this.entityInfoHelper = entityInfoHelper;
        this.initEntryListenerMap(entryListenerList);
    }

    @Nullable
    public EntryListener<?> findEntryListener(String schemaName, String tableName) {
        // 先根据数据库名找对应的 Map<String, EntryListener>
        Map<String, EntryListener<?>> listenerMap = MapValueUtil.getValueByRegex(ENTRY_LISTENER_MAP, schemaName);
        if (listenerMap == null) {
            listenerMap = MapValueUtil.getValueByRegex(ENTRY_LISTENER_MAP, DEFAULT_KEY);
        }
        // 再根据表名找对应的 EntryListener
        return listenerMap != null ? MapValueUtil.getValueByRegex(listenerMap, tableName) : null;
    }

    private void initEntryListenerMap(List<EntryListener<?>> entryListenerList) {
        Map<String, EntryListener<?>> defaultMap = new HashMap<>();
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
                defaultMap.put(tableName, entryListener);
                continue;
            }
            for (String schemaName : schemaNames) {
                Map<String, EntryListener<?>> map = new HashMap<>();
                map.put(tableName, entryListener);
                if (ENTRY_LISTENER_MAP.containsKey(schemaName)) {
                    ENTRY_LISTENER_MAP.get(schemaName).putAll(map);
                } else {
                    ENTRY_LISTENER_MAP.put(schemaName, map);
                }
            }
        }
        ENTRY_LISTENER_MAP.put(DEFAULT_KEY, defaultMap);
    }

}
