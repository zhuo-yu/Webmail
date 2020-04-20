package com.zy.webmail.coupon;

import com.zy.webmail.coupon.entity.CouponEntity;
import com.zy.webmail.coupon.service.CouponService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

@SpringBootTest
class WebmailCouponApplicationTests {

    @Autowired
    CouponService service;
    @Test
    void contextLoads() {
        CouponEntity couponEntity=new CouponEntity();
        couponEntity.setCouponName("一折优惠卷").setAmount(new BigDecimal(100));
        boolean save = service.save(couponEntity);
        System.out.println("插入状态"+save);
    }

}
