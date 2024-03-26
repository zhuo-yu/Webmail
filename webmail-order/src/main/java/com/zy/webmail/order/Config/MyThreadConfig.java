package com.zy.webmail.order.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class MyThreadConfig {

    @Bean
    public ThreadPoolExecutor threadPoolExecutor(MyThreadPoolProperties myThreadPoolProperties) {
        return new ThreadPoolExecutor(myThreadPoolProperties.getPoolSize(),
                myThreadPoolProperties.getMaxPoolSize(),
                myThreadPoolProperties.getKeepAlive(),
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(10000),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
    }

}
