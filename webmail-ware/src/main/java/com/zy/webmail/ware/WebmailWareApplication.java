package com.zy.webmail.ware;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class WebmailWareApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebmailWareApplication.class, args);
    }

}
