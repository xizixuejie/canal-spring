package io.xzxj.canal.spring.properties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xzxj
 * @date 2023/3/15 13:56
 */
public class CanalKafkaProperties {

    /**
     * kafka消费者订阅消息的topic和partition <br/>
     * key: topic <br/>
     * value: <code>","</code>分割的数组字符串 <br/>
     * 当该值不为空时，会忽略 {@link CanalKafkaProperties#topics} 属性
     */
    private Map<String, String> dynamicTopic = new HashMap<>();

    /**
     * kafka消费者订阅消息的topic
     */
    private List<String> topics = new ArrayList<>();

    private Integer partition;

    private String groupId;

    public Map<String, String> getDynamicTopic() {
        return dynamicTopic;
    }

    public void setDynamicTopic(Map<String, String> dynamicTopic) {
        this.dynamicTopic = dynamicTopic;
    }

    public List<String> getTopics() {
        return topics;
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }

    @Deprecated
    public Integer getPartition() {
        return partition;
    }

    /**
     * 如果需要设置topic分区，请使用dynamicTopic来指定
     *
     * @param partition topic分区
     */
    @Deprecated
    public void setPartition(Integer partition) {
        this.partition = partition;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

}
