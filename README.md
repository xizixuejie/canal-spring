[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/xizixuejie/canal-spring/blob/master/LICENSE)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.xizixuejie/canal-spring.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/io.github.xizixuejie/canal-spring/0.0.3)

# Canal-Spring

便于使用的canal-springboot客户端

## 快速开始

1. 引入maven依赖

   ```xml
   <dependency>
       <groupId>io.github.xizixuejie</groupId>
       <artifactId>canal-spring-boot-starter</artifactId>
       <version>0.0.3</version>
   </dependency>
   ```

2. 配置canal服务基本信息

   ```yaml
   canal:
     server: 127.0.0.1:11111
     destination: example
   ```

3. 在启动类添加注解 `@EnableCanalListener`

   `value`属性或者`basePackages` 属性指定扫描包路径。

4. 自定义CanalListener 实现 `io.xzxj.canal.core.listener.EntryListener` 接口

5. 实现类上增加注解 `@CanalListener(schemaName = "${database}",tableName = "${table_name}")` 

    `${database}` 监听数据库名

     `${table_name}` 监听表名。可以是正则表达式

    接口泛型是对应的实体类

6. 实现 `insert`  、 `update` 或者 `delete` 方法来监听你想做的操作



## 功能

打勾的是已实现的，未打勾的是以后会实现的

- [x] 实体类名自动转换表名
- [x] 跟据jpa或者mp注解自动转换实体类属性
- [x] tcp模式
- [x] kafka模式
- [ ] rocketMQ模式
- [x] rabbitMQ模式
- [ ] pulsarMQ模式



## 配置说明

| 属性                             | 描述                                                                                    | 默认值             |
|--------------------------------|---------------------------------------------------------------------------------------|-----------------|
| canal.server                   | canal服务地址<br />如果是Kafka模式为Kafka地址，多个地址以`,`分隔。<br />如果是rabbitMQ模式为rabbitMQ服务地址，目前只支持单个 | 127.0.0.1:11111 |
| canal.destination              | canal 的instance 名称<br />kafka模式为topic 名称<br />rabbitMQ模式为queue名称                      | example         |
| canal.filter                   | canal过滤的表名称，如配置则只订阅配置的表                                                               | ""              |
| canal.async                    | 是否是异步消费，异步消费时，消费时异常将导致消息不会回滚，也不保证顺序性                                                  | true            |
| canal.timeout                  | 消费的时间间隔(s)                                                                            | 1s              |
| canal.server-mode              | canal 客户端类型,目前支持 tcp,kafka,rabbitMQ类型                                                 | tcp             |
| canal.username                 | canal 的用户名                                                                            | null            |
| canal.password                 | canal 的密码                                                                             | null            |
| canal.kafka.group-id           | kafka groupId 消费者订阅消息时可使用，kafka canal 客户端                                             | null            |
| canal.kafka.partition          | kafka partition                                                                       | null            |
| canal.rabbitMq.virtualHost     | rabbitMq  virtualHost                                                                 | /               |
| canal.rabbitMq.queueName       | rabbitMq 队列名                                                                          | null            |
| canal.rabbitMq.accessKey       | 阿里的ak                                                                                 | ""              |
| canal.rabbitMq.secretKey       | 阿里的sk                                                                                 | ""              |
| canal.rabbitMq.resourceOwnerId | 资源owner账号（主账号                                                                         | null            |
| canal.rabbitMq.username        | rabbitMq username                                                                     | guest           |
| canal.rabbitMq.password        | rabbitMq password                                                                     | guest           |



## 更新记录

- 2023-05-04 更新v0.0.3版本。`@CanalListener` 注解的 `tableName` 属性可以用正则表达式。
- 2023-03-25 更新v0.0.2版本。实现了RabbitMQ类型客户端。
- 2023-03-22 更新v0.0.1版本。实现指定库名、表名监听，实体类属性自动转换，tcp和kafka类型客户端。



## 参考

- [Canal](https://github.com/alibaba/canal)
- [canal-client](https://github.com/NormanGyllenhaal/canal-client)
