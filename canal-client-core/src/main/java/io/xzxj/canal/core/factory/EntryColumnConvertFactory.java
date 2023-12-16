package io.xzxj.canal.core.factory;

import com.alibaba.otter.canal.protocol.CanalEntry;
import io.xzxj.canal.core.listener.EntryListener;
import io.xzxj.canal.core.metadata.AbstractEntityInfoHelper;
import io.xzxj.canal.core.util.TableFieldUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author xzxj
 * @date 2023/3/12 12:10
 */
public class EntryColumnConvertFactory extends AbstractConvertFactory<List<CanalEntry.Column>> {

    public EntryColumnConvertFactory(AbstractEntityInfoHelper entityInfoHelper) {
        super(entityInfoHelper);
    }

    @Override
    <R> R newInstance(Class<R> clazz, List<CanalEntry.Column> columnList) throws InstantiationException, IllegalAccessException, NoSuchFieldException {
        R object = clazz.newInstance();
        Map<String, String> fieldMap = entityInfoHelper.getFieldMap(object.getClass());
        for (CanalEntry.Column column : columnList) {
            String fieldName = fieldMap.get(column.getName());
            if (StringUtils.isNotEmpty(fieldName)) {
                TableFieldUtil.setFieldValue(object, fieldName, column.getValue());
            }
        }
        return object;
    }

    @Override
    public <R> R newInstance(EntryListener<?> entryHandler, List<CanalEntry.Column> columnList, Set<String> updateColumn) throws InstantiationException, IllegalAccessException, NoSuchFieldException {
        Class<R> tableClass = entityInfoHelper.getTableClass(entryHandler);
        if (tableClass == null) {
            return null;
        }
        R r = tableClass.newInstance();
        Map<String, String> columnNames = entityInfoHelper.getFieldMap(r.getClass());
        for (CanalEntry.Column column : columnList) {
            if (!updateColumn.contains(column.getName())) {
                continue;
            }
            String fieldName = columnNames.get(column.getName());
            if (StringUtils.isNotEmpty(fieldName)) {
                TableFieldUtil.setFieldValue(r, fieldName, column.getValue());
            }
        }
        return r;
    }

}
