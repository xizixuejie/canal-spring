package io.xzxj.canal.core.annotation;

import io.xzxj.canal.core.convertor.IColumnConvertor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ColumnConvertor {

    Class<? extends IColumnConvertor<?>> value();

}
