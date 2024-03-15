package io.xzxj.canal.spring.autoconfigure;

import io.xzxj.canal.core.client.AbstractMqCanalClient;
import io.xzxj.canal.core.client.KafkaCanalClient;
import io.xzxj.canal.core.handler.IMessageHandler;
import io.xzxj.canal.spring.client.CanalClientList;
import io.xzxj.canal.spring.properties.CanalProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * @author xzxj
 * @date 2023/3/15 13:51
 */
@ConditionalOnProperty(value = "canal.server-mode", havingValue = "kafka")
public class KafkaClientAutoConfiguration extends BaseMqCanalClientAutoConfiguration {

    public KafkaClientAutoConfiguration(CanalProperties canalProperties) {
        super(canalProperties);
    }

    @Bean(initMethod = "init", destroyMethod = "destroy")
    @ConditionalOnMissingBean(AbstractMqCanalClient.class)
    public CanalClientList kafkaCanalClientList(IMessageHandler<?> messageHandler) {
        CanalClientList list = new CanalClientList();
        String[] destinations = canalProperties.getDestination().split(",");
        KafkaCanalClient.Builder builder = KafkaCanalClient.builder().servers(canalProperties.getServer())
                .groupId(canalProperties.getKafka().getGroupId())
                .topics(canalProperties.getKafka().getTopics())
                .dynamicTopic(canalProperties.getKafka().getDynamicTopic())
                .messageHandler(messageHandler)
                .batchSize(canalProperties.getBatchSize())
                .filter(canalProperties.getFilter())
                .timeout(canalProperties.getTimeout())
                .unit(canalProperties.getUnit())
                .flatMessage(canalProperties.getMq().getFlatMessage());
        for (String destination : destinations) {
            list.add(builder.destination(destination).build());
        }
        return list;
    }


    @Bean(initMethod = "init", destroyMethod = "destroy")
    @ConditionalOnMissingBean({AbstractMqCanalClient.class, CanalClientList.class})
    public KafkaCanalClient kafkaCanalClient(IMessageHandler<?> messageHandler) {
        return KafkaCanalClient.builder().servers(canalProperties.getServer())
                .groupId(canalProperties.getKafka().getGroupId())
                .topics(canalProperties.getKafka().getTopics())
                .messageHandler(messageHandler)
                .batchSize(canalProperties.getBatchSize())
                .filter(canalProperties.getFilter())
                .timeout(canalProperties.getTimeout())
                .unit(canalProperties.getUnit())
                .flatMessage(canalProperties.getMq().getFlatMessage())
                .build();
    }

}
