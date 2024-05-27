[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/xizixuejie/canal-spring/blob/master/LICENSE)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.xizixuejie/canal-spring.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/io.github.xizixuejie/canal-spring)

# Canal-Spring

便于使用的canal-springboot客户端

还在测试使用阶段，如果在使用过程中发现问题，欢迎 [提交issue](https://github.com/xizixuejie/canal-spring/issues/new)
或者 [加入qq群](http://qm.qq.com/cgi-bin/qm/qr?_wv=1027&k=7Gvt3n_pwCIGgrFEz0uODds4v4tMkln6&authKey=bns6pJSmMy8nZ0HvAleVi%2Fh4um%2FEBChQmDmWDUii0Wrox9XrpYW7G%2FQV6xjv6Alo&noverify=0&group_code=286331326)
讨论

## 快速开始

1. 环境依赖

   jdk >= 1.8

   canal-server = 1.1.6

2. 引入maven依赖

   ```xml
   <dependency>
       <groupId>io.github.xizixuejie</groupId>
       <artifactId>canal-spring-boot-starter</artifactId>
       <version>0.0.16</version>
   </dependency>
   ```

3. 配置canal服务基本信息

   ```yaml
   canal:
     server: 127.0.0.1:11111
     destination: example
   ```

4. 在启动类添加注解 `@EnableCanalListener`

   `value`属性或者`basePackages` 属性指定扫描包路径。

5. 自定义CanalListener 实现 `io.xzxj.canal.core.listener.EntryListener` 接口

6. 实现类上增加注解 `@CanalListener(schemaName = "${database}", tableName = "${table_name}")`

    `${database}` 监听数据库名

     `${table_name}` 监听表名。可以是正则表达式

    接口泛型是对应的实体类

7. 实现 `insert`  、 `update` 或者 `delete` 方法来监听你想做的操作



### 示例代码

```java
@CanalListener
public class TestListener implements EntryListener<TestEntity> {
    private static final Logger log = LoggerFactory.getLogger(TestListener.class);

    @Override
    public void insert(TestEntity testEntity) {
        log.info("insert={}", testEntity);
    }

    @Override
    public void update(TestEntity before, TestEntity after) {
        log.info("update before={}", before);
        log.info("update after={}", after);
    }

    @Override
    public void delete(TestEntity testEntity) {
        log.info("delete={}", testEntity);
    }
}
```



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

### 自定义实体类属性类型转换

如果你的实体类属性不在 `io.xzxj.canal.core.convertor.impl` 这个包里实现，可以自定义类型转换器。

**全局配置方式**

1. 自定义bean实现 `io.xzxj.canal.core.convertor.IColumnConvertor<T>` ，泛型是你实体类的类型。
2. 实现 `convert` 方法

**注解单独指定**

优先级比全局高

1. 在实体类属性上添加 `@ColumnConvertor` 注解
2. value是 `io.xzxj.canal.core.convertor.IColumnConvertor<T>` 的实现类，需要有一个无参构造函数。

### @CanalTopicPartitions使用说明

> 只在kafka模式下生效

可以加在你的 `EntryListener<T>` 的实现类上，用来指定监听具体的哪个topic以及partition，如果只指定了topic，则会监听这个topic的所有partition。

## 配置说明

| 属性                                | 描述                                                                                    | 默认值             |
|-----------------------------------|---------------------------------------------------------------------------------------|-----------------|
| canal.server                      | canal服务地址<br />如果是Kafka模式为Kafka地址，多个地址以`,`分隔。<br />如果是rabbitMQ模式为rabbitMQ服务地址，目前只支持单个 | 127.0.0.1:11111 |
| canal.annotation-type             | 选择实体类上的注解类型                                                                           | mybatis_plus    |
| canal.destination                 | canal 的instance 名称<br />~~kafka模式为topic 名称~~<br />rabbitMQ模式为queue名称                  | example         |
| canal.filter                      | canal过滤的表名称，如配置则只订阅配置的表                                                               | ""              |
| canal.async                       | 是否是异步消费，异步消费时，消费时异常将导致消息不会回滚，也不保证顺序性                                                  | true            |
| canal.timeout                     | 消费的时间间隔(s)                                                                            | 1s              |
| canal.server-mode                 | canal 客户端类型,目前支持 tcp,kafka,rabbitMQ类型                                                 | tcp             |
| canal.username                    | canal 的用户名                                                                            | null            |
| canal.password                    | canal 的密码                                                                             | null            |
| canal.mq.flat-message             | JSON 消息格式                                                                             | true            |
| canal.kafka.group-id              | kafka groupId 消费者订阅消息时可使用，kafka canal 客户端                                             | null            |
| canal.kafka.dynamic-topic         | kafka消费者订阅消息的topic和partition，如果用这个属性指定topic配置，则会忽略canal.kafka.topics配置                | {}              |
| canal.kafka.topics                | kafka消费者订阅消息的topic                                                                    | []              |
| canal.kafka.partition             | kafka partition 已过时，如果需要设置topic分区，请使用dynamicTopic来指定                                  | null            |
| canal.rabbit-mq.virtual-host      | rabbitMq  virtualHost                                                                 | /               |
| canal.rabbit-mq.queue-name        | rabbitMq 队列名                                                                          | null            |
| canal.rabbit-mq.access-key        | 阿里的ak                                                                                 | ""              |
| canal.rabbit-mq.secret-key        | 阿里的sk                                                                                 | ""              |
| canal.rabbit-mq.resource-owner-id | 资源owner账号（主账号                                                                         | null            |
| canal.rabbit-mq.username          | rabbitMq username                                                                     | guest           |
| canal.rabbit-mq.password          | rabbitMq password                                                                     | guest           |



## 更新记录

- 2024-05-27 v0.0.16 子类继承父类的相同属性时，获取字段名称和实体属性的对应关系会出现异常的问题。
- 2024-05-07 v0.0.15 修改了获取类的属性，只能获取一层父类属性的问题。
- 2024-03-27 v0.0.14 修改了多个destination时，CanalClient销毁出现异常的问题
- 2024-03-15 v0.0.13 优化了异常处理；修改了spring自动配置
- 2024-03-12 v0.0.12 修改了tableName正则配置失效问题
- 2024-03-12 v0.0.11 修改了一些bug；kafka模式和tcp模式支持多个destination，而且在kafka模式下支持自定义topic、partition
- 2023-12-26 v0.0.10 修改了一些bug，支持一个表对应多个EntryListener，支持注解指定自定义实体类属性类型转换。
- 2023-12-18 v0.0.9 允许自定义实体类属性名称和类型转换。
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
