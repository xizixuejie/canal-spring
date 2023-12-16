package io.xzxj.canal.spring.autoconfigure;

import io.xzxj.canal.core.client.AbstractMqCanalClient;
import io.xzxj.canal.core.client.KafkaCanalClient;
import io.xzxj.canal.core.handler.IMessageHandler;
import io.xzxj.canal.spring.properties.CanalMqProperties;
import io.xzxj.canal.spring.properties.CanalProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.Optional;

/**
 * @author xzxj
 * @date 2023/3/15 13:51
 */
@EnableConfigurationProperties(CanalProperties.class)
@ConditionalOnProperty(value = "canal.server-mode", havingValue = "kafka")
@Import({ThreadPoolAutoConfiguration.class, CanalAutoConfiguration.class})
public class KafkaClientAutoConfiguration extends BaseMqCanalClientAutoConfiguration {

    public KafkaClientAutoConfiguration(CanalProperties canalProperties) {
        super(canalProperties);
    }

    @Bean(initMethod = "init", destroyMethod = "destroy")
    @ConditionalOnMissingBean(AbstractMqCanalClient.class)
    public KafkaCanalClient kafkaCanalClient(IMessageHandler<?> messageHandler) {
        Optional<CanalMqProperties> mqPropertiesOpt = Optional.ofNullable(canalProperties.getMq());
        return KafkaCanalClient.builder().servers(canalProperties.getServer())
                .groupId(canalProperties.getKafka().getGroupId())
                .topic(canalProperties.getDestination())
                .partition(canalProperties.getKafka().getPartition())
                .messageHandler(messageHandler)
                .batchSize(canalProperties.getBatchSize())
                .filter(canalProperties.getFilter())
                .timeout(canalProperties.getTimeout())
                .unit(canalProperties.getUnit())
                .flatMessage(mqPropertiesOpt.map(CanalMqProperties::getFlatMessage).orElse(Boolean.TRUE))
                .build();
    }

}
