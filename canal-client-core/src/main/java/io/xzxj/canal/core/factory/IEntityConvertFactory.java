package io.xzxj.canal.core.factory;

import io.xzxj.canal.core.listener.EntryListener;

import java.util.Set;

/**
 * 数据库字段转换实体类工厂
 *
 * @author xzxj
 * @date 2023/3/11 15:08
 */
public interface IEntityConvertFactory<T> {

    <R> R newInstance(EntryListener entryHandler, T t) throws Exception;

    default <R> R newInstance(EntryListener entryHandler, T t, Set<String> updateColumn) throws InstantiationException, IllegalAccessException, NoSuchFieldException {
        return null;
    }

}
