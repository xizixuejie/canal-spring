package io.xzxj.canal.spring.autoconfigure;

import io.xzxj.canal.core.client.AbstractMqCanalClient;
import io.xzxj.canal.core.client.RabbitMqCanalClient;
import io.xzxj.canal.core.handler.IMessageHandler;
import io.xzxj.canal.spring.properties.CanalMqProperties;
import io.xzxj.canal.spring.properties.CanalProperties;
import io.xzxj.canal.spring.properties.CanalRabbitMqProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

import java.util.Optional;

/**
 * @author xzxj
 * @date 2023/3/24 9:49
 */
@ConditionalOnProperty(value = "canal.server-mode", havingValue = "rabbit_mq")
public class RabbitMqClientAutoConfiguration extends BaseMqCanalClientAutoConfiguration {

    public RabbitMqClientAutoConfiguration(CanalProperties canalProperties) {
        super(canalProperties);
    }

    @Bean(initMethod = "init", destroyMethod = "destroy")
    @ConditionalOnMissingBean(AbstractMqCanalClient.class)
    public RabbitMqCanalClient rabbitMqCanalClient(IMessageHandler<?> messageHandler) {
        Optional<CanalMqProperties> mqPropertiesOpt = Optional.ofNullable(canalProperties.getMq());
        CanalRabbitMqProperties mqProperties = canalProperties.getRabbitMq();
        return RabbitMqCanalClient.builder().servers(canalProperties.getServer())
                .virtualHost(mqProperties.getVirtualHost())
                .queueName(canalProperties.getDestination())
                .accessKey(mqProperties.getAccessKey())
                .secretKey(mqProperties.getSecretKey())
                .resourceOwnerId(mqProperties.getResourceOwnerId())
                .username(mqProperties.getUsername())
                .password(mqProperties.getPassword())
                .messageHandler(messageHandler)
                .batchSize(canalProperties.getBatchSize())
                .filter(canalProperties.getFilter())
                .timeout(canalProperties.getTimeout())
                .unit(canalProperties.getUnit())
                .flatMessage(mqPropertiesOpt.map(CanalMqProperties::getFlatMessage).orElse(Boolean.TRUE))
                .build();
    }

}
