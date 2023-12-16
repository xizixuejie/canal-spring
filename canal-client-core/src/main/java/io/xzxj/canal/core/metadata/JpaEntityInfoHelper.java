package io.xzxj.canal.core.metadata;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.Column;
import javax.persistence.Table;
import java.beans.Transient;
import java.lang.reflect.Field;

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
        return field.getAnnotation(Transient.class) == null;
    }

    @Override
    protected String getColumn(Field field) {
        Column column = field.getAnnotation(Column.class);
        if (column != null && StringUtils.isNotBlank(column.name())) {
            return StringUtils.remove(column.name(), "`");
        }
        return defaultColumnName(field);
    }

}
