package io.xzxj.canal.core.convertor.impl;

import io.xzxj.canal.core.config.CanalEntityConvertConfig;
import io.xzxj.canal.core.convertor.IColumnConvertor;
import org.apache.commons.lang.time.DateUtils;

import javax.annotation.Nonnull;
import java.sql.Date;
import java.text.ParseException;

public class SqlDateColumnConvertor implements IColumnConvertor<Date> {

    @Override
    public Date convert(@Nonnull String value) {
        try {
            java.util.Date date = DateUtils.parseDate(value, CanalEntityConvertConfig.getInstance().getDateParsePatterns());
            return new Date(date.getTime());
        } catch (ParseException e) {
            return null;
        }
    }

}
