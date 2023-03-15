package io.xzxj.canal.example;

import io.xzxj.canal.spring.annotation.EnableCanalListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author xzxj
 * @date 2023/3/11 12:19
 */
@EnableCanalListener
@SpringBootApplication
public class ExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExampleApplication.class, args);
    }

}
