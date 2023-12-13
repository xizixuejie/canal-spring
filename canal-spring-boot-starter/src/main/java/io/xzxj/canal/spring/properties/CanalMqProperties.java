package io.xzxj.canal.spring.properties;

/**
 * @author Hision
 * @date 2023/12/12 21:25
 */
public class CanalMqProperties {

    /**
     * 是否 JSON 格式
     */
    protected Boolean flatMessage = Boolean.TRUE;

    public Boolean getFlatMessage() {
        return flatMessage;
    }

    public void setFlatMessage(Boolean flatMessage) {
        this.flatMessage = flatMessage;
    }
}
