package io.xzxj.canal.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author xzxj
 * @date 2023/3/11 10:27
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CanalListener {

    /**
     * 订阅表名 默认取注解实体类注解的 TableName
     */
    String value() default "";


    String name() default "";

}
