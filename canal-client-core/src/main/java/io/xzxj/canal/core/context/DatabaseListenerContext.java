package io.xzxj.canal.core.context;

import com.google.common.collect.Lists;
import io.xzxj.canal.core.listener.EntryListener;
import io.xzxj.canal.core.model.ListenerKey;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
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
     * key:     destination,schemaName,topic,partition
     * value:   数据库下的所有表的EntryListener
     */
    private final Map<ListenerKey, TableListenerMap> map = new HashMap<>();

    public void put(ListenerKey listenerKey, String tableName, EntryListener<?> listener) {
        map.compute(listenerKey, (k, v) -> {
            if (v == null) {
                v = new TableListenerMap();
            }
            v.put(tableName, listener);
            return v;
        });
    }

    public List<EntryListener<?>> getEntryListenersByTableName(ListenerKey listenerKey, String tableNameRegex) {
        TableListenerMap tableListenerMap = this.findTableListenerMap(listenerKey);

        if (tableListenerMap == null) {
            return Lists.newArrayList();
        }

        Set<EntryListener<?>> result = new HashSet<>();
        for (Map.Entry<String, List<EntryListener<?>>> entry : tableListenerMap.entrySet()) {
            if (Pattern.compile(entry.getKey()).matcher(tableNameRegex).matches()) {
                result.addAll(entry.getValue());
            }
        }
        return new ArrayList<>(result);
    }

    @Nullable
    private TableListenerMap findTableListenerMap(ListenerKey listenerKey) {
        List<ListenerKey.Builder> queryBuilders = Lists.newArrayList(
                new ListenerKey.Builder().destination(listenerKey.getDestination()),
                new ListenerKey.Builder().topic(listenerKey.getTopic()),
                new ListenerKey.Builder().partition(listenerKey.getPartition()),
                new ListenerKey.Builder().schemaName(listenerKey.getSchemaName()));

        // Generate combinations of query builders to cover all possible conditions
        List<List<ListenerKey.Builder>> combinations = generateCombinations(queryBuilders);

        TableListenerMap result = new TableListenerMap();
        for (List<ListenerKey.Builder> combination : combinations) {
            ListenerKey.Builder compositeBuilder = new ListenerKey.Builder();
            for (ListenerKey.Builder builder : combination) {
                compositeBuilder = compositeBuilder.merge(builder);
            }

            TableListenerMap tableListenerMap = map.get(compositeBuilder.build());
            if (tableListenerMap != null) {
                for (Map.Entry<String, List<EntryListener<?>>> entry : tableListenerMap.entrySet()) {
                    result.compute(entry.getKey(), (k, v) -> {
                        if (v == null) {
                            v = new ArrayList<>();
                        }
                        v.addAll(entry.getValue());
                        return v;
                    });
                }
            }
        }

        if (!result.isEmpty()) {
            return result;
        }

        return map.get(ListenerKey.empty());
    }

    // Helper method to generate combinations of query builders
    private List<List<ListenerKey.Builder>> generateCombinations(List<ListenerKey.Builder> queryBuilders) {
        List<List<ListenerKey.Builder>> combinations = new ArrayList<>();
        int n = queryBuilders.size();

        for (int i = 1; i <= n; i++) {
            combinations.addAll(generateCombinations(queryBuilders, i, 0, new ArrayList<>()));
        }

        return combinations;
    }

    private List<List<ListenerKey.Builder>> generateCombinations(
            List<ListenerKey.Builder> queryBuilders, int k, int start, List<ListenerKey.Builder> current) {
        List<List<ListenerKey.Builder>> combinations = new ArrayList<>();

        if (k == 0) {
            combinations.add(new ArrayList<>(current));
            return combinations;
        }

        for (int i = start; i < queryBuilders.size(); i++) {
            current.add(queryBuilders.get(i));
            combinations.addAll(generateCombinations(queryBuilders, k - 1, i + 1, current));
            current.remove(current.size() - 1);
        }

        return combinations;
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
