package io.xzxj.canal.core.util;

import com.baomidou.mybatisplus.annotation.TableName;
import com.google.common.base.CaseFormat;
import io.xzxj.canal.core.annotation.CanalListener;
import io.xzxj.canal.core.listener.EntryListener;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import javax.persistence.Table;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xzxj
 * @date 2023/3/11 16:19
 * @deprecated see {@link io.xzxj.canal.core.metadata.AbstractEntityInfoHelper}
 */
public class TableInfoUtil {

    private static Map<Class<? extends EntryListener>, Class>
            CLASS_LISTENER_CACHE_MAP = new ConcurrentHashMap<>();

    @Deprecated
    @Nullable
    public static String getTableName(EntryListener<?> entryListener) {
        CanalListener annotation = entryListener.getClass().getAnnotation(CanalListener.class);
        if (annotation == null) {
            return null;
        }
        StringBuilder fullName = new StringBuilder();
        String schemaName = annotation.schemaName().length != 0 ? annotation.schemaName()[0] : null;
        if (StringUtils.isNotBlank(schemaName)) {
            fullName.append(schemaName).append(".");
        }
        if (StringUtils.isNotBlank(annotation.tableName())) {
            fullName.append(annotation.tableName());
        } else {
            String tableName = findTableName(entryListener);
            fullName.append(tableName);
        }
        return fullName.toString();
    }

    @Nullable
    public static String findTableName(EntryListener<?> entryListener) {
        Class<Object> tableClass = getTableClass(entryListener);
        if (tableClass == null) {
            return null;
        }
        TableName tableName = tableClass.getAnnotation(TableName.class);
        if (tableName != null && StringUtils.isNotBlank(tableName.value())) {
            return tableName.value();
        }
        Table table = tableClass.getAnnotation(Table.class);
        if (table != null && StringUtils.isNotBlank(table.name())) {
            return table.name();
        }
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, tableClass.getName());
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
