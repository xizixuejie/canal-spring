package io.xzxj.canal.spring.properties;

/**
 * @author xzxj
 * @date 2023/3/24 9:36
 */
public class CanalRabbitMqProperties {

    // 主机名
    private String virtualHost = "/";

    // 一些鉴权信息
    private String accessKey = "";
    private String secretKey = "";
    private Long resourceOwnerId;
    private String username = "guest";
    private String password = "guest";

    public String getVirtualHost() {
        return virtualHost;
    }

    public void setVirtualHost(String virtualHost) {
        this.virtualHost = virtualHost;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public Long getResourceOwnerId() {
        return resourceOwnerId;
    }

    public void setResourceOwnerId(Long resourceOwnerId) {
        this.resourceOwnerId = resourceOwnerId;
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

}
