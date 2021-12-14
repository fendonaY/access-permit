package com.yyp;

import lombok.SneakyThrows;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.InputStreamReader;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class A {


    public static void main(String[] args) {
        SpringApplication.run(A.class, args);
    }

    @SneakyThrows
    @Bean
    public RedissonClient redissonClient() {
        ResourceLoader loder = new PathMatchingResourcePatternResolver();
        Config config = Config.fromYAML(new InputStreamReader(loder.getResource("redisson-config.yml").getInputStream()));
        //使用json序列化方式
        Codec codec = new JsonJacksonCodec();
        config.setCodec(codec);
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }
}
