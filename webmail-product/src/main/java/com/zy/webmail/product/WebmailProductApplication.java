package com.zy.webmail.product;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


/*
* 整合Mybatis-plus
* 1、导入依赖
* 2、配置：
*   2.1、配置数据源
*       1）导入数据库依赖
*       2）在application.yml配置数据源相关信息
*   2.2、配置mybatis-plus
*       1)使用mapperscan
*       2)告诉mybatis-plus映射文件在哪
*
* */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.zy.webmail.product.dao")
public class WebmailProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebmailProductApplication.class, args);
    }

}
