package io.xzxj.canal.spring.registrar;

import io.xzxj.canal.core.annotation.CanalListener;
import io.xzxj.canal.spring.annotation.EnableCanalListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.lang.NonNull;

/**
 * @author xzxj
 * @date 2023/3/14 19:47
 */
public class CanalListenerRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {

    private static final Logger log = LoggerFactory.getLogger(CanalListenerRegistrar.class);

    private ResourceLoader resourceLoader;

    @Override
    public void setResourceLoader(@NonNull ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void registerBeanDefinitions(@NonNull AnnotationMetadata metadata,
                                        @NonNull BeanDefinitionRegistry registry) {


        AnnotationAttributes annoAttrs = AnnotationAttributes.fromMap(metadata.getAnnotationAttributes(EnableCanalListener.class.getName()));
        // 获取到basePackage的值
        assert annoAttrs != null;
        String[] basePackages = annoAttrs.getStringArray("basePackages");
        // 如果没有设置basePackage 扫描路径,就扫描对应包下面的值
        if (basePackages.length == 0) {
            basePackages = new String[]{((StandardAnnotationMetadata) metadata).getIntrospectedClass().getPackage().getName()};
        }

        CanalListenerScanner scanHandle = new CanalListenerScanner(registry);

        if (resourceLoader != null) {
            scanHandle.setResourceLoader(resourceLoader);
        }
        // 这里实现的是根据名称来注入
        scanHandle.setBeanNameGenerator(new BeanNameGenerator());
        // 扫描指定路径下的接口
        scanHandle.doScan(basePackages);
    }

    static class BeanNameGenerator extends AnnotationBeanNameGenerator {
        @NonNull
        @Override
        public String generateBeanName(@NonNull BeanDefinition definition, @NonNull BeanDefinitionRegistry registry) {
            String name = getBeanNameByAnnotation(definition);
            if (name != null && !"".equals(name)) {
                return name;
            }
            return super.generateBeanName(definition, registry);
        }

        private String getBeanNameByAnnotation(BeanDefinition definition) {
            String beanClassName = definition.getBeanClassName();
            try {
                Class<?> clazz = Class.forName(beanClassName);
                CanalListener annotation = clazz.getAnnotation(CanalListener.class);
                if (annotation == null) {
                    return null;
                }
                return annotation.name();
            } catch (ClassNotFoundException e) {
                log.error(e.getMessage(), e);
            }
            return null;
        }
    }

}
