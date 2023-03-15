package io.xzxj.canal.spring.annotation;

import io.xzxj.canal.spring.registrar.CanalListenerRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author xzxj
 * @date 2023/3/11 16:40
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(CanalListenerRegistrar.class)
public @interface EnableCanalListener {

    String[] value() default {};

    String[] basePackages() default {};

}

