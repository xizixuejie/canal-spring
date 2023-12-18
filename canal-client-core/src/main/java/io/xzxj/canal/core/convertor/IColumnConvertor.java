package io.xzxj.canal.core.convertor;

import javax.annotation.Nonnull;

/**
 * 属性类型转换器接口
 *
 * @param <T> 属性class
 */
public interface IColumnConvertor<T> {

    /**
     * 转换属性类型
     *
     * @param value 属性值
     * @return 目标属性类型
     */
    T convert(@Nonnull String value);

}
