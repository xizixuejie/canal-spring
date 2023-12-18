package io.xzxj.canal.core.convertor.impl;

import io.xzxj.canal.core.config.CanalEntityConvertConfig;
import io.xzxj.canal.core.convertor.IColumnConvertor;
import org.apache.commons.lang.time.DateUtils;

import javax.annotation.Nonnull;
import java.text.ParseException;
import java.util.Date;


public class DateColumnConvertor implements IColumnConvertor<Date> {

    @Override
    public Date convert(@Nonnull String value) {
        try {
            return DateUtils.parseDate(value, CanalEntityConvertConfig.getInstance().getDateParsePatterns());
        } catch (ParseException var2) {
            return null;
        }
    }

}
