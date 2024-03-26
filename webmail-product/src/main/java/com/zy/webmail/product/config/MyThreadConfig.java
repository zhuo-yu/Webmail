package com.zy.webmail.product.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

@Configuration
public class MyThreadConfig {

    @Bean
    public ThreadPoolExecutor threadPoolExecutor(MyThreadPoolProperties myThreadPoolProperties){
       return new ThreadPoolExecutor(myThreadPoolProperties.getPoolSize(),
                myThreadPoolProperties.getMaxPoolSize(),
                myThreadPoolProperties.getKeepAlive(),
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(10000),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
    }

}
