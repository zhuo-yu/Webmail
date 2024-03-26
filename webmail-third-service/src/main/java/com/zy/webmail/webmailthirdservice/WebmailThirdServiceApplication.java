package com.zy.webmail.webmailthirdservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@EnableConfigurationProperties
public class WebmailThirdServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebmailThirdServiceApplication.class, args);
    }

}
