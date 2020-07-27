package com.zy.webmail.ware;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.zy.webmail.ware.dao")
public class WebmailWareApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebmailWareApplication.class, args);
    }

}
