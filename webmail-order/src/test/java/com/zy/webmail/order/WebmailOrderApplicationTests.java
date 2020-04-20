package com.zy.webmail.order;

import com.zy.webmail.order.entity.OrderEntity;
import com.zy.webmail.order.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class WebmailOrderApplicationTests {

    @Autowired
    OrderService orderService;

    @Test
    void contextLoads() {
        List<OrderEntity> list = orderService.list();
        System.out.println(list);
    }

}
