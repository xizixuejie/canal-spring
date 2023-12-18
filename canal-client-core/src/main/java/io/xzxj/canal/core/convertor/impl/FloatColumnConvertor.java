package io.xzxj.canal.core.convertor.impl;

import io.xzxj.canal.core.convertor.IColumnConvertor;

import javax.annotation.Nonnull;

public class FloatColumnConvertor implements IColumnConvertor<Float> {

    @Override
    public Float convert(@Nonnull String value) {
        return Float.parseFloat(value);
    }

}
