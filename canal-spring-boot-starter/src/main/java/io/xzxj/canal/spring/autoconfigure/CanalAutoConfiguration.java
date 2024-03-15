package io.xzxj.canal.spring.autoconfigure;

import io.xzxj.canal.core.config.CanalEntityConvertConfig;
import io.xzxj.canal.core.context.EntryListenerContext;
import io.xzxj.canal.core.convertor.IColumnConvertor;
import io.xzxj.canal.core.listener.EntryListener;
import io.xzxj.canal.core.metadata.AbstractEntityInfoHelper;
import io.xzxj.canal.core.metadata.JpaEntityInfoHelper;
import io.xzxj.canal.core.metadata.MyBatisPlusEntityInfoHelper;
import io.xzxj.canal.spring.properties.CanalProperties;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.List;

@EnableConfigurationProperties(CanalProperties.class)
@ImportAutoConfiguration({
        ThreadPoolAutoConfiguration.class,
        TcpClientAutoConfiguration.class,
        KafkaClientAutoConfiguration.class,
        RabbitMqClientAutoConfiguration.class,
})
public class CanalAutoConfiguration {

    @Bean
    @ConditionalOnProperty(value = "canal.annotation-type", havingValue = "mybatis_plus", matchIfMissing = true)
    @ConditionalOnMissingBean(AbstractEntityInfoHelper.class)
    public AbstractEntityInfoHelper myBatisPlusEntityInfoHelper() {
        return new MyBatisPlusEntityInfoHelper();
    }

    @Bean
    @ConditionalOnProperty(value = "canal.annotation-type", havingValue = "hibernate")
    @ConditionalOnMissingBean(AbstractEntityInfoHelper.class)
    public AbstractEntityInfoHelper jpaEntityInfoHelper() {
        return new JpaEntityInfoHelper();
    }

    @Bean
    public EntryListenerContext entryListenerContext(AbstractEntityInfoHelper entityInfoHelper,
                                                     List<EntryListener<?>> entryListenerList) {
        return new EntryListenerContext(entityInfoHelper, entryListenerList);
    }

    @Bean
    public CanalEntityConvertConfig canalEntityConvertConfig(List<IColumnConvertor<?>> columnConvertorList) {
        CanalEntityConvertConfig instance = CanalEntityConvertConfig.getInstance();
        instance.putAllColumnConvertor(columnConvertorList);
        return instance;
    }

}
