package com.lvfat;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@MapperScan({"com.lvfat.repository", "com.lvfat.mapper"})
public class LvFatApplication {

    public static void main(String[] args) {
        SpringApplication.run(LvFatApplication.class, args);
    }
}