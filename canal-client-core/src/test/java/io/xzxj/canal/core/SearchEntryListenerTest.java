package io.xzxj.canal.core;

import com.google.common.collect.Lists;
import io.xzxj.canal.core.annotation.CanalListener;
import io.xzxj.canal.core.annotation.CanalTopicPartition;
import io.xzxj.canal.core.annotation.CanalTopicPartitions;
import io.xzxj.canal.core.context.DatabaseListenerContext;
import io.xzxj.canal.core.context.EntryListenerContext;
import io.xzxj.canal.core.listener.EntryListener;
import io.xzxj.canal.core.metadata.JpaEntityInfoHelper;
import io.xzxj.canal.core.model.ListenerKey;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.Table;
import java.util.List;

class SearchEntryListenerTest {

    private DatabaseListenerContext databaseListenerContext;

    private final EntryListener<ExampleEntity> listener1 = new TestListener1();
    private final EntryListener<ExampleEntity> listener2 = new TestListener2();
    private final EntryListener<ExampleEntity> listener3 = new TestListener3();
    private final EntryListener<ExampleEntity> listener4 = new TestListener4();

    @BeforeEach
    void init() {
        new EntryListenerContext(new JpaEntityInfoHelper(),
                Lists.newArrayList(
                        listener1,
                        listener2,
                        listener3,
                        listener4
                ));
        databaseListenerContext = DatabaseListenerContext.getInstance();
    }

    @Test
    void testFindTableListener() {
        ListenerKey key = new ListenerKey.Builder()
                .destination("example")
                .topic("topic1")
                .partition("0")
                .schemaName("boot")
                .build();

        List<EntryListener<?>> expected = Lists.newArrayList(
                listener1,
                listener2
        );
        List<EntryListener<?>> actual = databaseListenerContext.getEntryListenersByTableName(key, "example");
        Assertions.assertIterableEquals(expected, actual, "没有找到对应的EntryListener");
    }


    @Table(name = "example")
    static class ExampleEntity {

    }

    @CanalListener(destination = {"example", "example2"}, topics = {"topic1", "topic2"})
    static class TestListener1 implements EntryListener<ExampleEntity> {
    }

    @CanalTopicPartitions(
            @CanalTopicPartition(topic = "topic1", partitions = {0, 1})
    )
    @CanalListener(destination = {"example"})
    static class TestListener2 implements EntryListener<ExampleEntity> {
    }

    @CanalTopicPartitions(
            @CanalTopicPartition(topic = "topic2", partitions = {0, 1})
    )
    @CanalListener(destination = "example", schemaName = "demo")
    static class TestListener3 implements EntryListener<ExampleEntity> {
    }

    @CanalTopicPartitions(
            @CanalTopicPartition(topic = "topic2", partitions = {1})
    )
    @CanalListener(schemaName = {"boot", "demo"})
    static class TestListener4 implements EntryListener<ExampleEntity> {
    }
}
