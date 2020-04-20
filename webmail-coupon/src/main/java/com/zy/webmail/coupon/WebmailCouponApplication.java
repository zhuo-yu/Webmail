package com.zy.webmail.coupon;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.FeignClient;

@SpringBootApplication
@MapperScan("com.zy.webmail.coupon.dao")
@EnableDiscoveryClient //开启服务注册与发现功能
public class WebmailCouponApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebmailCouponApplication.class, args);
    }

}
