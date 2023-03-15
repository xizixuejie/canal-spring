package io.xzxj.canal.spring.properties;

/**
 * @author xzxj
 * @date 2023/3/15 13:56
 */
public class CanalKafkaProperties {


    private Integer partition;

    private String groupId;

    public Integer getPartition() {
        return partition;
    }

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
