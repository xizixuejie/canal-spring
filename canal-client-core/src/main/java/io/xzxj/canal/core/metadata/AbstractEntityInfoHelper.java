package io.xzxj.canal.core.metadata;

import com.google.common.base.CaseFormat;
import io.xzxj.canal.core.listener.EntryListener;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@SuppressWarnings({"unchecked", "rawtypes"})
public abstract class AbstractEntityInfoHelper {

    /**
     * <p>key: EntryListener</p>
     * <p>value: EntryListener的泛型class</p>
     */
    protected static final Map<Class<? extends EntryListener>, Class> CLASS_LISTENER_CACHE_MAP = new ConcurrentHashMap<>();

    /**
     * 数据库实体类的属性map集合
     * key: 表名
     * value: 列名字：属性名
     */
    protected static final Map<Class<?>, Map<String, String>> TABLE_FILED_CACHE_MAP = new ConcurrentHashMap<>();

    /**
     * 获取默认表名，默认取实体类转下划线
     *
     * @param clazz 实体类class
     * @return 数据库表名
     */
    protected String defaultTableName(Class<?> clazz) {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, clazz.getName());
    }

    /**
     * 获取默认属性列名，默认取属性名转下划线
     *
     * @param field 实体类属性
     * @return 数据库列名
     */
    protected String defaultColumnName(Field field) {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, field.getName());
    }

    /**
     * 找到EntryListener泛型中的数据库实体类
     *
     * @param listener EntryListener的实现类
     * @param <T>      数据库实体类泛型
     * @return 数据库实体类class
     */
    @Nullable
    public <T> Class<T> getTableClass(EntryListener<?> listener) {
        Class<? extends EntryListener> listenerClass = listener.getClass();
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

    /**
     * 获取字段名称和实体属性的对应关系
     *
     * @param clazz 实体类
     * @return 列名和属性名对应关系map
     */
    public Map<String, String> getFieldMap(Class<?> clazz) {
        return TABLE_FILED_CACHE_MAP.computeIfAbsent(clazz, it -> {
            Map<String, String> map = new ConcurrentHashMap<>();
            // 获取所有字段（包括父类字段）
            List<Field> fields = FieldUtils.getAllFieldsList(it);
            for (Field field : fields) {
                // 跳过静态字段或非列字段
                if (!isColumnFiled(field) || Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                map.put(getColumnName(field), field.getName());
            }
            return map;
        });
    }

    public Set<String> getFields(Class<?> clazz, Collection<String> columns) {
        Map<String, String> fieldMap = this.getFieldMap(clazz);
        return columns.stream().map(fieldMap::get).collect(Collectors.toSet());
    }

    /**
     * 获取数据库表名
     *
     * @param entryListener EntryListener的实现类
     * @return 数据库表名
     */
    public String getTableName(EntryListener<?> entryListener) {
        Class<?> clazz = this.getTableClass(entryListener);
        return this.getTableName(clazz);
    }

    public String getColumnName(Field field) {
        String columnName = this.getColumn(field);
        StringUtils.remove(columnName, "`");
        return columnName;
    }

    /**
     * 获取数据库表名
     *
     * @param clazz 实体类
     * @return 数据库表名
     */
    public abstract String getTableName(Class<?> clazz);

    /**
     * 是否为数据库列
     *
     * @param field 属性
     * @return 如果是false，忽略这个属性
     */
    public abstract boolean isColumnFiled(Field field);

    /**
     * 获取对应的数据库列名
     *
     * @param field 属性
     * @return 数据库列名
     */
    protected abstract String getColumn(Field field);

}
