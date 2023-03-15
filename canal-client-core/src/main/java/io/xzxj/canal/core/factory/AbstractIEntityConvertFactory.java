package io.xzxj.canal.core.factory;

import io.xzxj.canal.core.listener.EntryListener;
import io.xzxj.canal.core.util.TableInfoUtil;

/**
 * @author xzxj
 * @date 2023/3/11 16:16
 */
public abstract class AbstractIEntityConvertFactory<T> implements IEntityConvertFactory<T> {

    @Override
    public <R> R newInstance(EntryListener entryListener, T t) throws Exception {
        //String tableName = TableInfoUtil.getTableName(entryListener);
        Class<R> tableClass = TableInfoUtil.getTableClass(entryListener);
        if (tableClass != null) {
            return newInstance(tableClass, t);
        }
        return null;
    }

    abstract <R> R newInstance(Class<R> clazz, T t) throws InstantiationException, IllegalAccessException, NoSuchFieldException;

}
