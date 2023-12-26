package io.xzxj.canal.core.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({})
@Retention(RetentionPolicy.RUNTIME)
public @interface CanalTopicPartition {

    String topic();

    int[] partitions() default {0};

}
