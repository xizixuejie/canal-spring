package io.xzxj.canal.spring.properties;

import io.xzxj.canal.spring.enums.CanalServerMode;
import io.xzxj.canal.spring.enums.OrmAnnotationType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.concurrent.TimeUnit;

/**
 * @author xzxj
 * @date 2023/3/11 11:36
 */
@ConfigurationProperties("canal")
public class CanalProperties {

    private CanalServerMode serverMode = CanalServerMode.TCP;

    private OrmAnnotationType annotationType = OrmAnnotationType.MYBATIS_PLUS;

    private Boolean async = true;

    private String destination = "example";

    private String server = "127.0.0.1:11111";

    private String filter = "";

    private String username;

    private String password;

    private Integer batchSize = 1;

    private Long timeout = 1L;

    private TimeUnit unit = TimeUnit.SECONDS;

    @NestedConfigurationProperty
    private CanalMqProperties mq;

    @NestedConfigurationProperty
    private CanalKafkaProperties kafka;

    @NestedConfigurationProperty
    private CanalRabbitMqProperties rabbitMq;

    public CanalServerMode getServerMode() {
        return serverMode;
    }

    public void setServerMode(CanalServerMode serverMode) {
        this.serverMode = serverMode;
    }

    public OrmAnnotationType getAnnotationType() {
        return annotationType;
    }

    public void setAnnotationType(OrmAnnotationType annotationType) {
        this.annotationType = annotationType;
    }

    public Boolean getAsync() {
        return async;
    }

    public void setAsync(Boolean async) {
        this.async = async;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(Integer batchSize) {
        this.batchSize = batchSize;
    }

    public Long getTimeout() {
        return timeout;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }

    public TimeUnit getUnit() {
        return unit;
    }

    public void setUnit(TimeUnit unit) {
        this.unit = unit;
    }

    public CanalMqProperties getMq() {
        return mq;
    }

    public void setMq(CanalMqProperties mq) {
        this.mq = mq;
    }

    public CanalKafkaProperties getKafka() {
        return kafka;
    }

    public void setKafka(CanalKafkaProperties kafka) {
        this.kafka = kafka;
    }

    public CanalRabbitMqProperties getRabbitMq() {
        return rabbitMq;
    }

    public void setRabbitMq(CanalRabbitMqProperties rabbitMq) {
        this.rabbitMq = rabbitMq;
    }

}
