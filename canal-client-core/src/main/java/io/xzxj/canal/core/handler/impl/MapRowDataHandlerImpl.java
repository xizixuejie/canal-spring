package io.xzxj.canal.core.handler.impl;

import com.alibaba.otter.canal.protocol.CanalEntry;
import io.xzxj.canal.core.factory.IConvertFactory;
import io.xzxj.canal.core.handler.RowDataHandler;
import io.xzxj.canal.core.listener.EntryListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * @author xzxj
 * @date 2023/3/15 14:06
 */
public class MapRowDataHandlerImpl implements RowDataHandler<List<Map<String, String>>> {

    private static final Logger log = LoggerFactory.getLogger(MapRowDataHandlerImpl.class);

    private final IConvertFactory<Map<String, String>> convertFactory;

    public MapRowDataHandlerImpl(IConvertFactory<Map<String, String>> convertFactory) {
        this.convertFactory = convertFactory;
    }

    @Override
    public <R> void handleRowData(List<Map<String, String>> mapList,
                                  EntryListener<R> entryListener,
                                  CanalEntry.EventType eventType) throws Exception {
        if (entryListener == null) {
            return;
        }
        switch (eventType) {
            case INSERT:
                R entry = convertFactory.newInstance(entryListener, mapList.get(0));
                entryListener.insert(entry);
                break;
            case UPDATE:
                R before = convertFactory.newInstance(entryListener, mapList.get(1));
                R after = convertFactory.newInstance(entryListener, mapList.get(0));
                entryListener.update(before, after);
                break;
            case DELETE:
                R o = convertFactory.newInstance(entryListener, mapList.get(0));
                entryListener.delete(o);
                break;
            default:
                log.info("未知消息类型 {} 不处理 {}", eventType, mapList);
                break;
        }
    }

}
