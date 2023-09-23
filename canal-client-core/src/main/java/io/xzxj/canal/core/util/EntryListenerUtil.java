package io.xzxj.canal.core.util;

import io.xzxj.canal.core.annotation.CanalListener;
import io.xzxj.canal.core.listener.EntryListener;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xzxj
 * @date 2023/8/16 11:54
 */
public class EntryListenerUtil {

    public static final String DEFAULT_KEY = "DEFAULT";

    private static Map<String, Map<String, EntryListener<?>>> entryListenerMap;

    @Nullable
    public static EntryListener<?> findEntryListener(List<EntryListener<?>> entryListenerList, String schemaName, String tableName) {
        if (entryListenerMap == null) {
            initEntryListenerMap(entryListenerList);
        }
        // 先根据数据库名找对应的 Map<String, EntryListener>
        Map<String, EntryListener<?>> listenerMap = MapValueUtil.getValueByRegex(entryListenerMap, schemaName);
        if (listenerMap == null) {
            listenerMap = MapValueUtil.getValueByRegex(entryListenerMap, EntryListenerUtil.DEFAULT_KEY);
        }

        // 再根据表名找对应的 EntryListener
        return listenerMap != null ? MapValueUtil.getValueByRegex(listenerMap, tableName) : null;
    }

    private static void initEntryListenerMap(List<EntryListener<?>> entryListenerList) {
        entryListenerMap = new HashMap<>();
        Map<String, EntryListener<?>> defaultMap = new HashMap<>();
        for (EntryListener<?> entryListener : entryListenerList) {
            CanalListener annotation = entryListener.getClass().getAnnotation(CanalListener.class);
            if (annotation == null) {
                continue;
            }
            String[] schemaNames = annotation.schemaName();
            String tableName = annotation.tableName();
            if (StringUtils.isBlank(tableName)) {
                tableName = TableInfoUtil.findTableName(entryListener);
            }
            if (schemaNames.length == 0) {
                defaultMap.put(tableName, entryListener);
                continue;
            }
            for (String schemaName : schemaNames) {
                Map<String, EntryListener<?>> map = new HashMap<>();
                map.put(tableName, entryListener);
                if (entryListenerMap.containsKey(schemaName)){
                    entryListenerMap.get(schemaName).putAll(map);
                }else {
                    entryListenerMap.put(schemaName, map);
                }
            }
        }
        entryListenerMap.put(DEFAULT_KEY, defaultMap);
    }

}
