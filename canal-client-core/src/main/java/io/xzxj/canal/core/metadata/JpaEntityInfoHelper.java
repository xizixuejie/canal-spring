package io.xzxj.canal.core.metadata;

import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Optional;

public final class JpaEntityInfoHelper extends AbstractEntityInfoHelper {

    @Override
    public String getTableName(Class<?> clazz) {
        Table annotation = clazz.getAnnotation(Table.class);
        if (annotation != null && StringUtils.isNotBlank(annotation.name())) {
            return annotation.name();
        }
        return defaultTableName(clazz);
    }

    @Override
    public boolean isColumnFiled(Field field) {
        Transient annotation = Optional.ofNullable(this.filedGetMethod(field))
                .map(m -> m.getAnnotation(Transient.class))
                .orElse(field.getAnnotation(Transient.class));
        return annotation == null;
    }

    @Override
    protected String getColumn(Field field) {
        Column column = Optional.ofNullable(this.filedGetMethod(field))
                .map(m -> m.getAnnotation(Column.class))
                .orElse(field.getAnnotation(Column.class));

        if (column != null && StringUtils.isNotBlank(column.name())) {
            return StringUtils.remove(column.name(), "`");
        }
        return defaultColumnName(field);
    }

    /**
     * 获取属性对应的get方法
     *
     * @param field 属性
     * @return get方法
     */
    @Nullable
    private Method filedGetMethod(@Nonnull Field field) {
        Class<?> clazz = field.getDeclaringClass();
        String fieldName = field.getName();
        String first = fieldName.substring(0, 1);
        String other = fieldName.substring(1);
        String methodName = "get" + first.toUpperCase(Locale.ROOT) + other;
        try {
            return clazz.getDeclaredMethod(methodName);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

}
