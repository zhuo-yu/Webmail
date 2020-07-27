package com.zy.elasticsearch.webmailelasticsearch;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zy.elasticsearch.webmailelasticsearch.config.elasticsearchconfig;
import lombok.Data;
import lombok.experimental.Accessors;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;

@SpringBootTest
class WebmailElasticsearchApplicationTests {

    @Resource
    private RestHighLevelClient client;


    @Test
    public void Search() throws IOException {
        SearchRequest searchRequest = new SearchRequest();  //创建检索请求
        searchRequest.indices("bank");   //指定索引

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();   //检索条件
        searchSourceBuilder.query(QueryBuilders.matchQuery("address","mill")); //match 检索
        /*聚合条件*/
        searchSourceBuilder.aggregation(AggregationBuilders.terms("age").field("age").size(10)).aggregation(AggregationBuilders.avg("ageavg").field("age"));
//        searchSourceBuilder.from(5);
//        searchSourceBuilder.size(5);

        searchRequest.source(searchSourceBuilder);  //放置检索条件
        SearchResponse search = client.search(searchRequest, elasticsearchconfig.COMMON_OPTIONS);//执行查询操作,返回结果
        System.out.println(search);
//        JSONObject jsonObject = JSON.parseObject(String.valueOf(search));

        //获取查到的数据
        SearchHit[] hits = search.getHits().getHits();
        for (SearchHit i:
                hits) {
            System.out.println("检索到的信息:"+i.getSourceAsString());   //打印获得的查询数据
        }
        //获取分析的数据
        Terms age = search.getAggregations().get("age");  //Terms分析对应Terms对象
        for (Terms.Bucket bucket : age.getBuckets()) {
            System.out.println(bucket.getKey());
        }
        Avg ageavg = search.getAggregations().get("ageavg");   //avg分析对应avg对象
        double value = ageavg.getValue();
        System.out.println(value);
    }

    @Test
    void index() throws IOException {
        IndexRequest indexRequest = new IndexRequest("users");
        indexRequest.id("1");
//        indexRequest.source("username","zhangsan","age","12","gender","男");   //一种方式
        testData testData = new testData();
        testData.setUsername("zhangsan").setAge(12).setGender("男");
        String string = JSON.toJSONString(testData);
        indexRequest.source(string, XContentType.JSON);
        //执行操作
        IndexResponse index = client.index(indexRequest, elasticsearchconfig.COMMON_OPTIONS);
        //打印相应数据
        System.out.println(index);
    }

    @Accessors(chain = true)
    @Data
    class testData{
        private String username;
        private String gender;
        private Integer age;
    }
    @Test
    void contextLoads() {
        System.out.println(client);
    }

}
