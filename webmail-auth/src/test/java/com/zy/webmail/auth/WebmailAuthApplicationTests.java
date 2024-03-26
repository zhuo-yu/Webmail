package com.zy.webmail.auth;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class WebmailAuthApplicationTests {

    private static ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(10);

    @Test
    void contextLoads() {
//        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(10);
        scheduledThreadPoolExecutor.scheduleAtFixedRate(()->{
            System.out.println("执行");
        },2,2, TimeUnit.SECONDS);
    }

}
