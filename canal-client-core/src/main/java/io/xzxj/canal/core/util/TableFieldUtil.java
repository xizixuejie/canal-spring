package io.xzxj.canal.core.util;

import com.google.common.base.CaseFormat;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.beans.Transient;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author xzxj
 * @date 2023/3/12 12:16
 */
public class TableFieldUtil {

    private static Map<Class<?>, Map<String, String>> TABLE_FILED_CACHE_MAP = new ConcurrentHashMap<>();

    /**
     * 获取字段名称和实体属性的对应关系
     *
     * @param clazz
     * @return
     */
    public static Map<String, String> getFieldMap(Class<?> clazz) {
        Map<String, String> map = TABLE_FILED_CACHE_MAP.get(clazz);
        if (map == null) {
            List<Field> fields = FieldUtils.getAllFieldsList(clazz);
            //如果实体类中存在column 注解，则使用column注解的名称为字段名
            map = fields.stream().filter(TableFieldUtil::notTransient)
                    .filter(field -> !Modifier.isStatic(field.getModifiers()))
                    .collect(Collectors.toMap(TableFieldUtil::getColumnName, Field::getName));
            TABLE_FILED_CACHE_MAP.putIfAbsent(clazz, map);
        }
        return map;
    }

    /**
     * TODO: mybatis或jpa注解
     * @param field
     * @return
     */
    private static String getColumnName(Field field) {
        //Column annotation = field.getAnnotation(Column.class);
        //return annotation != null ? annotation.name() : CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, field.getName());
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, field.getName());
    }

    private static boolean notTransient(Field field) {
        Transient annotation = field.getAnnotation(Transient.class);
        return annotation == null;
    }


    public static <R> void setFieldValue(R object, String fieldName, String value) throws NoSuchFieldException, IllegalAccessException {
        Field field;
        try{
            field = object.getClass().getDeclaredField(fieldName);
        }catch (NoSuchFieldException e){
            field = object.getClass().getSuperclass().getDeclaredField(fieldName);
        }
        field.setAccessible(true);
        Class<?> type = field.getType();
        Object result = StringConvertUtil.convertType(type, value);
        field.set(object,result);
    }

}
