package io.xzxj.canal.spring.registrar;

import io.xzxj.canal.core.annotation.CanalListener;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @author xzxj
 * @date 2023/3/16 14:13
 */
public class CanalListenerScanner extends ClassPathBeanDefinitionScanner {

    public CanalListenerScanner(BeanDefinitionRegistry registry) {
        super(registry);
    }

    @NonNull
    @Override
    protected Set<BeanDefinitionHolder> doScan(@NonNull String... basePackages) {
        // 添加过滤条件，这里是只添加了@CanalListener的注解才会被扫描到
        addIncludeFilter(new AnnotationTypeFilter(CanalListener.class));
        // 添加排除条件，忽略已经添加了@Component的类
        addExcludeFilter(new AnnotationTypeFilter(Component.class));
        // 调用spring的扫描
        return super.doScan(basePackages);
    }

}
