package io.xzxj.canal.core.util;

import io.xzxj.canal.core.annotation.CanalListener;
import io.xzxj.canal.core.listener.EntryListener;

import javax.annotation.Nullable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xzxj
 * @date 2023/3/11 16:19
 */
public class TableInfoUtil {

    private static Map<Class<? extends EntryListener>, Class>
            CLASS_LISTENER_CACHE_MAP = new ConcurrentHashMap<>();

    @Nullable
    public static String getTableName(EntryListener<?> entryListener) {
        CanalListener canalTable = entryListener.getClass().getAnnotation(CanalListener.class);
        if (canalTable != null) {
            return canalTable.value();
        }
        return null;
    }

    /**
     * 找到EntryListener泛型中的数据库实体类
     *
     * @param object
     * @param <T>
     * @return
     */
    @Nullable
    public static <T> Class<T> getTableClass(EntryListener<?> object) {
        Class<? extends EntryListener> listenerClass = object.getClass();
        Class<T> tableClass = CLASS_LISTENER_CACHE_MAP.get(listenerClass);
        if (tableClass != null) {
            return tableClass;
        }
        Type[] interfacesTypes = listenerClass.getGenericInterfaces();
        for (Type type : interfacesTypes) {
            Class<?> c = (Class<?>) ((ParameterizedType) type).getRawType();
            if (c.equals(EntryListener.class)) {
                tableClass = (Class<T>) ((ParameterizedType) type).getActualTypeArguments()[0];
                CLASS_LISTENER_CACHE_MAP.putIfAbsent(listenerClass, tableClass);
                return tableClass;
            }
        }
        return null;
    }

}
