package io.xzxj.canal.core.util;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.google.common.base.CaseFormat;
import io.xzxj.canal.core.config.CanalEntityConvertConfig;
import io.xzxj.canal.core.convertor.IColumnConvertor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Column;
import java.beans.Transient;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author xzxj
 * @date 2023/3/12 12:16
 */
public class TableFieldUtil {

    private static final Logger log = LoggerFactory.getLogger(TableFieldUtil.class);

    private static final Map<Class<?>, Map<String, String>> TABLE_FILED_CACHE_MAP = new ConcurrentHashMap<>();

    /**
     * 获取字段名称和实体属性的对应关系
     *
     * @param clazz
     * @return
     * @deprecated see {@link io.xzxj.canal.core.metadata.AbstractEntityInfoHelper#getFieldMap(Class)}
     */
    public static Map<String, String> getFieldMap(Class<?> clazz) {
        Map<String, String> map = TABLE_FILED_CACHE_MAP.get(clazz);
        if (map == null) {
            List<Field> fields = FieldUtils.getAllFieldsList(clazz);
            //如果实体类中存在column 注解，则使用注解的名称为字段名
            map = fields.stream().filter(TableFieldUtil::tableColumnFiled)
                    .filter(field -> !Modifier.isStatic(field.getModifiers()))
                    .collect(Collectors.toMap(TableFieldUtil::getColumnName, Field::getName));
            TABLE_FILED_CACHE_MAP.putIfAbsent(clazz, map);
        }
        return map;
    }

    /**
     * @param field
     * @return
     * @deprecated see {@link io.xzxj.canal.core.metadata.AbstractEntityInfoHelper#getColumnName(Field)}
     */
    private static String getColumnName(Field field) {
        TableId tableId = field.getAnnotation(TableId.class);
        if (tableId != null && StringUtils.isNotBlank(tableId.value())) {
            return StringUtils.remove(tableId.value(), "`");
        }
        TableField tableField = field.getAnnotation(TableField.class);
        if (tableField != null && StringUtils.isNotBlank(tableField.value())) {
            return StringUtils.remove(tableField.value(), "`");
        }
        Column column = field.getAnnotation(Column.class);
        if (column != null && StringUtils.isNotBlank(column.name())) {
            return StringUtils.remove(column.name(), "`");
        }
        return defaultColumnName(field);
    }

    private static String defaultColumnName(Field field) {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, field.getName());
    }

    /**
     * @param field
     * @return
     * @deprecated see {@link io.xzxj.canal.core.metadata.AbstractEntityInfoHelper#isColumnFiled(Field)}
     */
    private static boolean tableColumnFiled(Field field) {
        TableField tableField = field.getAnnotation(TableField.class);
        Transient annotation = field.getAnnotation(Transient.class);
        return tableField == null || tableField.exist() || annotation == null;
    }

    public static <R> void setFieldValue(R object, String fieldName, String value) throws NoSuchFieldException, IllegalAccessException {
        Field field;
        try {
            field = object.getClass().getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            field = object.getClass().getSuperclass().getDeclaredField(fieldName);
        }
        field.setAccessible(true);
        Object result = convertType(field.getGenericType(), value);
        field.set(object, result);
    }

    /**
     * 转换属性类型
     *
     * @param type  字段类型class
     * @param value 属性值
     * @return 转换后的类型
     */
    static Object convertType(Type type, String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        if (String.class.equals(type)) {
            return value;
        }
        String typeName = type.getTypeName();
        IColumnConvertor<?> convertor = CanalEntityConvertConfig.getInstance().getColumnConvertor(typeName);
        if (convertor == null) {
            log.warn("类型: {}没有找到对应的转换类", typeName);
            return value;
        }
        return convertor.convert(value);
    }

}
