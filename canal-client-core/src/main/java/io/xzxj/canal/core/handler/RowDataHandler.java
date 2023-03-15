package io.xzxj.canal.core.handler;

import com.alibaba.otter.canal.protocol.CanalEntry;
import io.xzxj.canal.core.listener.EntryListener;

/**
 * @author xzxj
 * @date 2023/3/11 14:57
 */
public interface RowDataHandler<T> {

    <R> void handleRowData(T t, EntryListener<R> entryListener, CanalEntry.EventType eventType) throws Exception;

}
