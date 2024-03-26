package com.zy.elasticsearch.webmailelasticsearch.Service.impl;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zy.common.to.es.SkuEsModel;
import com.zy.elasticsearch.webmailelasticsearch.Service.ProductSaveService;
import com.zy.elasticsearch.webmailelasticsearch.config.elasticsearchconfig;
import com.zy.elasticsearch.webmailelasticsearch.constant.Esconstant;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductSaveServiceimpl implements ProductSaveService {
    @Resource
    RestHighLevelClient client;
    @Override
    public boolean productStatusUp(List<SkuEsModel> esModels) throws IOException {
        //保存到es
        //1、给es建立索引,建立好映射关系 mapping
        //2、给es保存数据
        //BulkRequest bulkRequest, RequestOptions options
        BulkRequest bulkRequest = new BulkRequest();  //使用批量操作
        for (SkuEsModel esModel : esModels) {
            //构造保存请求
            IndexRequest indexRequest = new IndexRequest(Esconstant.PRODUCT_INDEXT); //指定索引或创建的索引
            indexRequest.id(esModel.getSkuId().toString()) ; //指定唯一id
            String s = JSON.toJSONString(esModel);   //将对象转换为JSON
            indexRequest.source(s, XContentType.JSON);
            bulkRequest.add(indexRequest);  //将所有索引操作添加进批量操作
        }
            //执行批量操作
        BulkResponse bulk = client.bulk(bulkRequest, elasticsearchconfig.COMMON_OPTIONS);
        boolean b = bulk.hasFailures();
        if(!b){
            List<String> collect = Arrays.stream(bulk.getItems()).map(item -> {
                return item.getId();
            }).collect(Collectors.toList());
            log.error("商品上架完成:{}",collect);
            return b;
        }else {
            return false;
        }


    }
}
