package io.xzxj.canal.example.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author xzxj
 * @date 2023/3/11 13:53
 */
@Data
@TableName("t_test")
public class TestEntity {

    private Long id;

    private String name;

    @TableField("f_age")
    private Integer age;

}
