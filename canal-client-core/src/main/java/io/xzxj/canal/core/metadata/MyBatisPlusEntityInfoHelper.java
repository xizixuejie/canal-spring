package io.xzxj.canal.core.metadata;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;

public final class MyBatisPlusEntityInfoHelper extends AbstractEntityInfoHelper {

    @Override
    public String getTableName(Class<?> clazz) {
        TableName tableName = clazz.getAnnotation(TableName.class);
        if (tableName != null && StringUtils.isNotBlank(tableName.value())) {
            return tableName.value();
        }
        return defaultTableName(clazz);
    }

    @Override
    public boolean isColumnFiled(Field field) {
        TableField tableField = field.getAnnotation(TableField.class);
        return tableField == null || tableField.exist();
    }

    @Override
    protected String getColumn(Field field) {
        TableId tableId = field.getAnnotation(TableId.class);
        if (tableId != null && StringUtils.isNotBlank(tableId.value())) {
            return StringUtils.remove(tableId.value(), "`");
        }
        TableField tableField = field.getAnnotation(TableField.class);
        if (tableField != null && StringUtils.isNotBlank(tableField.value())) {
            return StringUtils.remove(tableField.value(), "`");
        }
        return defaultColumnName(field);
    }

}
