package io.xzxj.canal.core.util;

import java.util.Map;

/**
 * @author xzxj
 * @date 2023/5/4 10:13
 */
public class MapValueUtil {

    public static <V> V getValueByRegex(Map<String, V> map, String tableName) {
        for (Map.Entry<String, V> entry : map.entrySet()) {
            String key = entry.getKey();
            if (key == null) {
                continue;
            }
            if (tableName.matches(key)) {
                return entry.getValue();
            }
        }
        return null;
    }

}
