package io.xzxj.canal.core.convertor.impl;

import io.xzxj.canal.core.convertor.IColumnConvertor;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;

public class ByteArrayColumnConvertor implements IColumnConvertor<byte[]> {

    @Override
    public byte[] convert(@Nonnull String value) {
        return value.getBytes(StandardCharsets.UTF_8);
    }

}
