package com.zy.webmail.product;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zy.webmail.product.entity.BrandEntity;
import com.zy.webmail.product.service.BrandService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class WebmailProductApplicationTests {

    @Autowired
    BrandService brandService;

    @Test
    void contextLoads() {
        BrandEntity brandEntity=new BrandEntity();
//        brandEntity.setName("华为");
//        boolean save = brandService.save(brandEntity);
//        System.out.println("插入状态:"+save);

//        brandEntity.setBrandId(1L).setDescript("华为");
//        boolean b = brandService.updateById(brandEntity);
//        System.out.println("更新状态"+b);

        List<BrandEntity> list = brandService.list(new QueryWrapper<BrandEntity>().eq("brand_id", 1L));
        list.forEach((item)->{
            System.out.println(item);
        });
    }

}
