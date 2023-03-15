package io.xzxj.canal.core.listener;

/**
 * @author xzxj
 * @date 2023/3/11 10:44
 */
public interface EntryListener<T> {

    /**
     * 监听到新增数据时会触发的方法
     *
     * @param t 数据库新增的数据
     */
    default void insert(T t) {

    }

    /**
     * 监听到修改数据时会触发的方法
     *
     * @param before 修改之前的数据
     * @param after  修改之后的数据
     */
    default void update(T before, T after) {

    }

    /**
     * 监听到删除数据时会触发的方法
     *
     * @param t 数据库删除的数据
     */
    default void delete(T t) {

    }

}
