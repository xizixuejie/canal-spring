package io.xzxj.canal.core.convertor.impl;

import io.xzxj.canal.core.convertor.IColumnConvertor;

import javax.annotation.Nonnull;

public class DoubleColumnConvertor implements IColumnConvertor<Double> {

    @Override
    public Double convert(@Nonnull String value) {
        return Double.parseDouble(value);
    }

}
