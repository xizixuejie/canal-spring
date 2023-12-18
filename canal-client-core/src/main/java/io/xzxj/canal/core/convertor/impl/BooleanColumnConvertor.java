package io.xzxj.canal.core.convertor.impl;

import io.xzxj.canal.core.convertor.IColumnConvertor;

import javax.annotation.Nonnull;

public class BooleanColumnConvertor implements IColumnConvertor<Boolean> {

    @Override
    public Boolean convert(@Nonnull String value) {
        return "1".equalsIgnoreCase(value) || "true".equalsIgnoreCase(value);
    }

}
