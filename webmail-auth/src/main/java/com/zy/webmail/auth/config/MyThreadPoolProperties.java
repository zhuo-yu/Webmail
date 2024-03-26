package com.zy.webmail.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 线程池配置
 */
@Component
@Data
@ConfigurationProperties(prefix = "webmail.thread")
public class MyThreadPoolProperties {

    /** 核心线程数 */
    private Integer poolSize;

    /** 最大线程数量*/
    private Integer maxPoolSize;

    /** 存活时间 */
    private Long keepAlive;

    private Integer scheduledThreadSize;
}
