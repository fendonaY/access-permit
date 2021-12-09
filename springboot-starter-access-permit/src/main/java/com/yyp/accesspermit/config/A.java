package com.yyp.accesspermit.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@Slf4j
public class A {

    public static void main(String[] args) {
        log.info("--------------------start--------------------");
        SpringApplication.run(A.class, args);
        log.info("--------------------end--------------------");
    }
}
