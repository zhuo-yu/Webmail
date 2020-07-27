package com.zy.webmail;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/*
*  开启远程调用功能
*       1、引入feign依赖
*       2、编写接口，告诉springcloud这个接口需要远程调用
*       3、声明接口的每一个方法需要调用哪个远程服务的哪个方法
*       4、开启远程调用功能
* */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.zy.webmail.member.feign")  //开启远程调用功能
public class WebmailMemberApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebmailMemberApplication.class, args);
    }

}
