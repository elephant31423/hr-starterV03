package com.example.hrstarter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "logExecutor")
    public Executor logExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);        // 核心執行緒數
        executor.setMaxPoolSize(10);       // 最大執行緒數
        executor.setQueueCapacity(1000);   // 隊列容量
        executor.setThreadNamePrefix("LogThread-");
        executor.initialize();
        return executor;
    }
}