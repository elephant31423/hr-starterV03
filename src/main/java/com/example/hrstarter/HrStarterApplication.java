package com.example.hrstarter;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
@MapperScan("com.example.hrstarter.mapper")
public class HrStarterApplication {
    public static void main(String[] args) {
        SpringApplication.run(HrStarterApplication.class, args);
    }
}
