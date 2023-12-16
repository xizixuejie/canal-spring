package io.xzxj.canal.core.factory;

import io.xzxj.canal.core.listener.EntryListener;
import io.xzxj.canal.core.metadata.AbstractEntityInfoHelper;

/**
 * @author xzxj
 * @date 2023/3/11 16:16
 */
public abstract class AbstractConvertFactory<T> implements IConvertFactory<T> {

    protected final AbstractEntityInfoHelper entityInfoHelper;

    public AbstractConvertFactory(AbstractEntityInfoHelper entityInfoHelper) {
        this.entityInfoHelper = entityInfoHelper;
    }

    @Override
    public <R> R newInstance(EntryListener<?> entryListener, T t) throws Exception {
        Class<R> tableClass = entityInfoHelper.getTableClass(entryListener);
        if (tableClass != null) {
            return newInstance(tableClass, t);
        }
        return null;
    }

    abstract <R> R newInstance(Class<R> clazz, T t) throws InstantiationException, IllegalAccessException, NoSuchFieldException;

}
