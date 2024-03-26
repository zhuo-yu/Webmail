package com.zy.webmail.order;

import com.zy.webmail.order.entity.OrderEntity;
import com.zy.webmail.order.service.OrderService;
import org.apache.commons.lang.time.DateUtils;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class WebmailOrderApplicationTests {

    @Autowired
    OrderService orderService;

    @Autowired
    AmqpAdmin amqpAdmin;

    @Autowired
    AmqpTemplate amqpTemplate;

    @Test
    void contextLoads() {
        List<OrderEntity> list = orderService.list();
        System.out.println(list);
    }

    @Test
    void createExchange(){
        amqpAdmin.declareExchange(new DirectExchange("zy.direct"));
        amqpAdmin.declareQueue(new Queue("zyqueues.news"));
        amqpAdmin.declareBinding(new Binding("zyqueues.news",Binding.DestinationType.QUEUE,"zy.direct","zyqueues.news",null));
    }

    @Test
    void sendMessage(){
//        amqpTemplate.convertAndSend();
    }

}
