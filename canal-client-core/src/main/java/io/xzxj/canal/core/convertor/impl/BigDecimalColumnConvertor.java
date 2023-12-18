package io.xzxj.canal.core.convertor.impl;

import io.xzxj.canal.core.convertor.IColumnConvertor;

import javax.annotation.Nonnull;
import java.math.BigDecimal;

public class BigDecimalColumnConvertor implements IColumnConvertor<BigDecimal> {

    @Override
    public BigDecimal convert(@Nonnull String value) {
        return new BigDecimal(value);
    }

}
