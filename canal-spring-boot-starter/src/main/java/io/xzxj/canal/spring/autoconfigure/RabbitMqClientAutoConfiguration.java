package io.xzxj.canal.spring.autoconfigure;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.FlatMessage;
import com.alibaba.otter.canal.protocol.Message;
import io.xzxj.canal.core.client.AbstractMqCanalClient;
import io.xzxj.canal.core.client.RabbitMqCanalClient;
import io.xzxj.canal.core.context.EntryListenerContext;
import io.xzxj.canal.core.factory.EntryColumnConvertFactory;
import io.xzxj.canal.core.handler.IMessageHandler;
import io.xzxj.canal.core.handler.RowDataHandler;
import io.xzxj.canal.core.handler.impl.AsyncFlatMessageHandlerImpl;
import io.xzxj.canal.core.handler.impl.AsyncMessageHandlerImpl;
import io.xzxj.canal.core.handler.impl.RowDataHandlerImpl;
import io.xzxj.canal.core.metadata.AbstractEntityInfoHelper;
import io.xzxj.canal.spring.properties.CanalMqProperties;
import io.xzxj.canal.spring.properties.CanalProperties;
import io.xzxj.canal.spring.properties.CanalRabbitMqProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

/**
 * @author xzxj
 * @date 2023/3/24 9:49
 */
@EnableConfigurationProperties(CanalProperties.class)
@ConditionalOnProperty(value = "canal.server-mode", havingValue = "rabbit_mq")
@Import({ThreadPoolAutoConfiguration.class, CanalAutoConfiguration.class})
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
