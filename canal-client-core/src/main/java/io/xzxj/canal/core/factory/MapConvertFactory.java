package io.xzxj.canal.core.factory;

import io.xzxj.canal.core.util.TableFieldUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * @author xzxj
 * @date 2023/3/15 14:10
 */
public class MapConvertFactory extends AbstractConvertFactory<Map<String, String>> {

    @Override
    <R> R newInstance(Class<R> clazz, Map<String, String> valueMap) throws InstantiationException, IllegalAccessException, NoSuchFieldException {
        R object = clazz.newInstance();
        Map<String, String> fieldMap = TableFieldUtil.getFieldMap(object.getClass());
        for (Map.Entry<String, String> entry : valueMap.entrySet()) {
            String fieldName = fieldMap.get(entry.getKey());
            if (StringUtils.isNotEmpty(fieldName)) {
                TableFieldUtil.setFieldValue(object, fieldName, entry.getValue());
            }
        }
        return object;
    }

}
