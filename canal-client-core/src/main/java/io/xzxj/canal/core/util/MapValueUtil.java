package io.xzxj.canal.core.util;

import java.util.Map;

/**
 * @author xzxj
 * @date 2023/5/4 10:13
 */
public class MapValueUtil {

    public static <V> V getValueByRegex(Map<String, V> map, String key) {
        for (Map.Entry<String, V> entry : map.entrySet()) {
            String entryKey = entry.getKey();
            if (entryKey == null) {
                continue;
            }
            if (key.matches(entryKey)) {
                return entry.getValue();
            }
        }
        return null;
    }

}
