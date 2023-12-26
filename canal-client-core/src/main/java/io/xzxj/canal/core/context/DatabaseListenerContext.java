package io.xzxj.canal.core.context;

import io.xzxj.canal.core.listener.EntryListener;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 存放数据库名，表名和EntryListener的对应关系的容器
 */
public final class DatabaseListenerContext {

    private static DatabaseListenerContext instance = null;

    private DatabaseListenerContext() {
    }

    public static DatabaseListenerContext getInstance() {
        if (instance == null) {
            instance = new DatabaseListenerContext();
        }
        return instance;
    }

    /**
     * key:     数据库名字
     * value:   数据库下的所有表的EntryListener
     */
    private final Map<String, TableListenerMap> map = new HashMap<>();

    public static final String DEFAULT_DATABASE_NAME = "DEFAULT";

    public void put(String tableName, EntryListener<?> listener) {
        this.put(DEFAULT_DATABASE_NAME, tableName, listener);
    }

    public void put(String dbName, String tableName, EntryListener<?> listener) {
        TableListenerMap dbListeners = map.getOrDefault(dbName, new TableListenerMap());
        dbListeners.put(tableName, listener);
        map.put(dbName, dbListeners);
    }

    public List<EntryListener<?>> getListenersByRegex(String dbName, String tableNameRegex) {
        // 先根据数据库名找对应的 EntryListener
        TableListenerMap dbListeners = map.get(dbName);
        if (dbListeners == null) {
            // 如果没有查询到，查询默认的数据库名字的 EntryListener
            dbListeners = map.get(DEFAULT_DATABASE_NAME);
        }
        if (dbListeners == null) {
            return new ArrayList<>();
        }
        List<EntryListener<?>> result = new ArrayList<>();
        // 再根据表名找对应的 EntryListener
        for (Map.Entry<String, List<EntryListener<?>>> entry : dbListeners.entrySet()) {
            if (Pattern.compile(tableNameRegex).matcher(entry.getKey()).matches()) {
                result.addAll(entry.getValue());
            }
        }
        return result;
    }

    static class TableListenerMap implements Map<String, List<EntryListener<?>>> {

        /**
         * key:     表名字
         * value:   对应的EntryListener集合
         */
        private final Map<String, List<EntryListener<?>>> map = new HashMap<>();

        @Override
        public int size() {
            return map.size();
        }

        @Override
        public boolean isEmpty() {
            return map.isEmpty();
        }

        @Override
        public boolean containsKey(Object key) {
            return map.containsKey(key);
        }

        @Override
        public boolean containsValue(Object value) {
            return map.containsValue(value);
        }

        @Override
        public List<EntryListener<?>> get(Object key) {
            return map.get(key);
        }

        public void put(String key, EntryListener<?> value) {
            List<EntryListener<?>> list = map.getOrDefault(key, new ArrayList<>());
            list.add(value);
            map.put(key, list);
        }

        @Override
        public List<EntryListener<?>> put(String key, List<EntryListener<?>> value) {
            return map.put(key, value);
        }

        @Override
        public List<EntryListener<?>> remove(Object key) {
            return map.remove(key);
        }

        @Override
        public void putAll(@Nonnull Map<? extends String, ? extends List<EntryListener<?>>> m) {
            map.putAll(m);
        }

        @Override
        public void clear() {
            map.clear();
        }

        @Nonnull
        @Override
        public Set<String> keySet() {
            return map.keySet();
        }

        @Nonnull
        @Override
        public Collection<List<EntryListener<?>>> values() {
            return map.values();
        }

        @Nonnull
        @Override
        public Set<Entry<String, List<EntryListener<?>>>> entrySet() {
            return map.entrySet();
        }

    }

}