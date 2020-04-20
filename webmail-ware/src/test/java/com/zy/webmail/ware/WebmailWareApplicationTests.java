package com.zy.webmail.ware;

import com.zy.webmail.ware.entity.WareInfoEntity;
import com.zy.webmail.ware.service.WareInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class WebmailWareApplicationTests {

    @Autowired
    WareInfoService wareInfoService;
    @Test
    void contextLoads() {
        List<WareInfoEntity> list = wareInfoService.list();
        System.out.println(list);
    }

}
