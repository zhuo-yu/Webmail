package com.zy.webmail.product;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zy.webmail.product.entity.BrandEntity;
import com.zy.webmail.product.service.BrandService;
import com.zy.webmail.product.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

@SpringBootTest
class WebmailProductApplicationTests {

    @Autowired
    BrandService brandService;

    @Autowired
    CategoryService categoryService;

    @Test
    public void test(){
        Long[] getcatelogpath = categoryService.getcatelogpath(166L);
        System.out.println(Arrays.asList(getcatelogpath));
    }
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

    @Test
    void test1(){
        HashMap<Object, Object> map = new HashMap<>();
        map.put("k","v");
        Hashtable<Object, Object> hashtable = new Hashtable<>(map);
        System.out.println(map.size()+","+hashtable.size());
    }

    @Test
    public int reverse(int x) {
            String x2=Integer.toString(x);
            try{
                if(x<0){
                    String substring = x2.substring(1);
                    return Integer.parseInt((new StringBuilder(substring).reverse().toString()))*-1;
                }else{
                    return Integer.parseInt(new StringBuilder(x2).reverse().toString());
                }
            }catch(Exception e){
                return 0;
            }

    }
    @Test
    void test2(){
        System.out.println(reverse(-123));
    }


}
