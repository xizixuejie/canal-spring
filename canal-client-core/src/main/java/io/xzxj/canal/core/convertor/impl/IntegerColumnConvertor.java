package io.xzxj.canal.core.convertor.impl;

import io.xzxj.canal.core.convertor.IColumnConvertor;

import javax.annotation.Nonnull;

public class IntegerColumnConvertor implements IColumnConvertor<Integer> {

    @Override
    public Integer convert(@Nonnull String value) {
        return Integer.parseInt(value);
    }

}
