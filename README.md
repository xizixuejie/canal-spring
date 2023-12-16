[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/xizixuejie/canal-spring/blob/master/LICENSE)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.xizixuejie/canal-spring.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/io.github.xizixuejie/canal-spring)

# Canal-Spring

便于使用的canal-springboot客户端

## 快速开始

1. 引入maven依赖

   ```xml
   <dependency>
       <groupId>io.github.xizixuejie</groupId>
       <artifactId>canal-spring-boot-starter</artifactId>
       <version>0.0.8</version>
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

5. 实现类上增加注解 `@CanalListener(schemaName = "${database}", tableName = "${table_name}")` 

    `${database}` 监听数据库名

     `${table_name}` 监听表名。可以是正则表达式

    接口泛型是对应的实体类

6. 实现 `insert`  、 `update` 或者 `delete` 方法来监听你想做的操作



## 功能

打勾的是已实现的，未打勾的是以后会实现的

- [x] 实体类名自动转换表名
- [x] 跟据jpa或者mp注解自动转换实体类属性，或者其他orm框架的注解（需要自定义）
- [x] tcp模式
- [x] kafka模式
- [ ] rocketMQ模式
- [x] rabbitMQ模式
- [ ] pulsarMQ模式



### 自定义实体类属性列名字转换

如果你用的orm框架不是mybatis-plus或者spring-data-jpa，可以自定义一个转换器。

1. 自定义bean 继承 `io.xzxj.canal.core.metadata.AbstractEntityInfoHelper` 
2. 实现抽象类中的三个方法
   - 获取数据库表名 `getTableName`
   - 是否为数据库列 `isColumnFiled`
   - 获取对应的数据库列名 `getColumn`



## 配置说明

| 属性                                | 描述                                                                                    | 默认值             |
|-----------------------------------|---------------------------------------------------------------------------------------|-----------------|
| canal.server                      | canal服务地址<br />如果是Kafka模式为Kafka地址，多个地址以`,`分隔。<br />如果是rabbitMQ模式为rabbitMQ服务地址，目前只支持单个 | 127.0.0.1:11111 |
| canal.annotation-type             | 选择实体类上的注解类型                                                                           | mybatis_plus    |
| canal.destination                 | canal 的instance 名称<br />kafka模式为topic 名称<br />rabbitMQ模式为queue名称                      | example         |
| canal.filter                      | canal过滤的表名称，如配置则只订阅配置的表                                                               | ""              |
| canal.async                       | 是否是异步消费，异步消费时，消费时异常将导致消息不会回滚，也不保证顺序性                                                  | true            |
| canal.timeout                     | 消费的时间间隔(s)                                                                            | 1s              |
| canal.server-mode                 | canal 客户端类型,目前支持 tcp,kafka,rabbitMQ类型                                                 | tcp             |
| canal.username                    | canal 的用户名                                                                            | null            |
| canal.password                    | canal 的密码                                                                             | null            |
| canal.mq.flat-message             | JSON 消息格式                                                                             | true            |
| canal.kafka.group-id              | kafka groupId 消费者订阅消息时可使用，kafka canal 客户端                                             | null            |
| canal.kafka.partition             | kafka partition                                                                       | null            |
| canal.rabbit-mq.virtual-host      | rabbitMq  virtualHost                                                                 | /               |
| canal.rabbit-mq.queue-name        | rabbitMq 队列名                                                                          | null            |
| canal.rabbit-mq.access-key        | 阿里的ak                                                                                 | ""              |
| canal.rabbit-mq.secret-key        | 阿里的sk                                                                                 | ""              |
| canal.rabbit-mq.resource-owner-id | 资源owner账号（主账号                                                                         | null            |
| canal.rabbit-mq.username          | rabbitMq username                                                                     | guest           |
| canal.rabbit-mq.password          | rabbitMq password                                                                     | guest           |



## 更新记录

- 2023-12-13 v0.0.8 优化Listener处理过程；mq模式配置flat-message。
- 2023-09-25 v0.0.7 优化定义多个同库不同表Listener导致只能获取到最后一个Listener信息的问题。
- 2023-09-01 v0.0.6 增加List字段转换，修改了默认tcp模式自动配置不生效的问题。
- 2023-08-16 更新v0.0.5版本。`@CanalListener` 注解的 `schemaName` 属性可以指定多个，也可以使用正则表达。
- 2023-05-31 更新v0.0.4版本。修改消费消息异常处理。
- 2023-05-04 更新v0.0.3版本。`@CanalListener` 注解的 `tableName` 属性可以用正则表达式。
- 2023-03-25 更新v0.0.2版本。实现了RabbitMQ类型客户端。
- 2023-03-22 更新v0.0.1版本。实现指定库名、表名监听，实体类属性自动转换，tcp和kafka类型客户端。



## 参考

- [Canal](https://github.com/alibaba/canal)
- [canal-client](https://github.com/NormanGyllenhaal/canal-client)
