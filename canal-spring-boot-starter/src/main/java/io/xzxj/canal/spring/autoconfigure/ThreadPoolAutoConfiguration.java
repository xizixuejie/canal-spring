package io.xzxj.canal.spring.autoconfigure;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author xzxj
 * @date 2023/3/15 10:42
 */
@Configuration
@ConditionalOnProperty(value = "canal.async", havingValue = "true", matchIfMissing = true)
public class ThreadPoolAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(ThreadPoolAutoConfiguration.class);

    @Bean(destroyMethod = "shutdown")
    public ExecutorService executorService() {
        BasicThreadFactory factory = new BasicThreadFactory.Builder()
                .namingPattern("canal-execute-thread-%d")
                .uncaughtExceptionHandler((t, e) -> log.error("thread " + t.getName() + " have a exception", e))
                .build();

        return new ThreadPoolExecutor(20, 100,0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(1024), factory, new ThreadPoolExecutor.AbortPolicy());
    }

}
