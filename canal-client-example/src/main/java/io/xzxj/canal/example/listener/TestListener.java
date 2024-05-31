package io.xzxj.canal.example.listener;

import io.xzxj.canal.core.annotation.CanalListener;
import io.xzxj.canal.core.listener.EntryListener;
import io.xzxj.canal.example.entity.TestEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author xzxj
 * @date 2023/3/11 13:53
 */
@Component
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
