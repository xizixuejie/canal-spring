package io.xzxj.canal.core.handler.impl;

import com.alibaba.otter.canal.protocol.CanalEntry;
import io.xzxj.canal.core.factory.IEntityConvertFactory;
import io.xzxj.canal.core.handler.RowDataHandler;
import io.xzxj.canal.core.listener.EntryListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author xzxj
 * @date 2023/3/12 11:55
 */
public class RowDataHandlerImpl implements RowDataHandler<CanalEntry.RowData> {

    private static final Logger log = LoggerFactory.getLogger(RowDataHandlerImpl.class);

    private final IEntityConvertFactory<List<CanalEntry.Column>> modelFactory;

    public RowDataHandlerImpl(IEntityConvertFactory modelFactory) {
        this.modelFactory = modelFactory;
    }

    @Override
    public <R> void handleRowData(CanalEntry.RowData rowData, EntryListener<R> entryListener, CanalEntry.EventType eventType) throws Exception {
        if (entryListener == null) {
            log.warn("entryListener not found");
            return;
        }
        switch (eventType) {
            case INSERT:
                R object = modelFactory.newInstance(entryListener, rowData.getAfterColumnsList());
                entryListener.insert(object);
                break;
            case UPDATE:
                Set<String> updateColumnSet = rowData.getAfterColumnsList().stream().filter(CanalEntry.Column::getUpdated)
                        .map(CanalEntry.Column::getName).collect(Collectors.toSet());
                R before = modelFactory.newInstance(entryListener, rowData.getBeforeColumnsList(), updateColumnSet);
                R after = modelFactory.newInstance(entryListener, rowData.getAfterColumnsList());
                entryListener.update(before, after);
                break;
            case DELETE:
                R o = modelFactory.newInstance(entryListener, rowData.getBeforeColumnsList());
                entryListener.delete(o);
                break;
            default:
                break;
        }
    }
}
