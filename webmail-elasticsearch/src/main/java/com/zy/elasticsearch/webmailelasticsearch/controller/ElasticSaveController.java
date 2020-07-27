package com.zy.elasticsearch.webmailelasticsearch.controller;

import com.zy.common.exception.exceptionenum;
import com.zy.common.to.es.SkuEsModel;
import com.zy.common.utils.R;
import com.zy.elasticsearch.webmailelasticsearch.Service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RequestMapping("search/save")
@RestController
public class ElasticSaveController {

    @Resource
    ProductSaveService productSaveService;
    //上架商品
    @PostMapping("/product")
    public R productStatusUp(@RequestBody List<SkuEsModel> esModels){
        try{
            productSaveService.productStatusUp(esModels);
        }catch (Exception e){
            log.error("ElasticSaveController商品上架错误:{}",e);
            return  R.error(exceptionenum.PRODUCT_UP_EXCEPTION.getCode(),exceptionenum.PRODUCT_UP_EXCEPTION.getMsg());
        }
        return R.ok();
    }
}
