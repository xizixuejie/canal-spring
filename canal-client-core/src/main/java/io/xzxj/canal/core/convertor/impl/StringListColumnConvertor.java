package io.xzxj.canal.core.convertor.impl;

import com.alibaba.fastjson2.JSON;
import io.xzxj.canal.core.convertor.IColumnConvertor;

import javax.annotation.Nonnull;
import java.util.List;

public class StringListColumnConvertor implements IColumnConvertor<List<String>> {

    @Override
    public List<String> convert(@Nonnull String value) {
        return JSON.parseArray(value, String.class);
    }

}
