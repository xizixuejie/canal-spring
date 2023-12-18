package io.xzxj.canal.core.convertor.impl;

import io.xzxj.canal.core.convertor.IColumnConvertor;

import javax.annotation.Nonnull;

public class LongColumnConvertor implements IColumnConvertor<Long> {

    @Override
    public Long convert(@Nonnull String value) {
        return Long.parseLong(value);
    }

}
