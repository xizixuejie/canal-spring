package io.xzxj.canal.core.handler.impl;

import com.alibaba.otter.canal.protocol.CanalEntry;
import io.xzxj.canal.core.factory.IConvertFactory;
import io.xzxj.canal.core.handler.RowDataHandler;
import io.xzxj.canal.core.listener.EntryListener;
import io.xzxj.canal.core.metadata.AbstractEntityInfoHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author xzxj
 * @date 2023/3/15 14:06
 */
public class MapRowDataHandlerImpl implements RowDataHandler<List<Map<String, String>>> {

    private static final Logger log = LoggerFactory.getLogger(MapRowDataHandlerImpl.class);

    private final IConvertFactory<Map<String, String>> convertFactory;
    private final AbstractEntityInfoHelper entityInfoHelper;

    public MapRowDataHandlerImpl(IConvertFactory<Map<String, String>> convertFactory,
                                 AbstractEntityInfoHelper entityInfoHelper) {
        this.convertFactory = convertFactory;
        this.entityInfoHelper = entityInfoHelper;
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
                Map<String, String> old = mapList.get(1);
                R before = convertFactory.newInstance(entryListener, old);
                R after = convertFactory.newInstance(entryListener, mapList.get(0));
                Set<String> fields = entityInfoHelper.getFields(before.getClass(), old.keySet());
                entryListener.update(before, after, fields);
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
