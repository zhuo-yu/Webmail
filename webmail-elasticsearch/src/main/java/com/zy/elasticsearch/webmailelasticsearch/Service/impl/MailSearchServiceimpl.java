package com.zy.elasticsearch.webmailelasticsearch.Service.impl;

import com.alibaba.fastjson.JSON;
import com.zy.common.to.es.SkuEsModel;
import com.zy.elasticsearch.webmailelasticsearch.Service.MailSearchService;
import com.zy.elasticsearch.webmailelasticsearch.config.elasticsearchconfig;
import com.zy.elasticsearch.webmailelasticsearch.constant.Esconstant;
import com.zy.elasticsearch.webmailelasticsearch.vo.*;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.lucene.search.function.FunctionScoreQuery;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.elasticsearch.search.sort.SortOrder;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MailSearchServiceimpl implements MailSearchService {

    @Autowired
    RestHighLevelClient restHighLevelClient;

    @Override
    public SearchResult search(SearchParams params) {
        //动态构建出查询需要的DSL语句
        SearchResult searchResult = null;
        //准备检索需求
        SearchRequest searchRequest = buildSearchRequest(params);

        try {
            //执行检索请求
            SearchResponse response = restHighLevelClient.search(searchRequest, elasticsearchconfig.COMMON_OPTIONS);

            //分析相应数据封装成我们需要的格式
            searchResult = buildSearchResult(response,params);
            //TODO
        }catch (Exception e){
            e.printStackTrace();
        }
        return searchResult;
    }

    /**
     * 分析相应数据封装成我们需要的格式
     * @return
     */
    private SearchResult buildSearchResult(SearchResponse response,SearchParams params) {
        SearchHits hits = response.getHits();


        SearchResult result = new SearchResult();
        //查询到的所有商品信息
        List<SkuEsModel> productList = new ArrayList<>();
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit : searchHits) {
            SkuEsModel skuEsModel = JSON.parseObject(hit.getSourceAsString(), SkuEsModel.class);
            if (StringUtils.isNotBlank(params.getKeyword())){
                String skuTitle = hit.getHighlightFields().get("skuTitle").getFragments()[0].string();
                skuEsModel.setSkuTitle(skuTitle);
            }
            productList.add(skuEsModel);
        }
        result.setProducts(productList);

        Aggregations aggregations = response.getAggregations();
        //当前查询到的结果,所有涉及的品牌
        List<BrandVo> brandList = new ArrayList<>();
        ParsedLongTerms brandAgg = aggregations.get("brandAgg");
        for (Terms.Bucket bucket : brandAgg.getBuckets()) {
            BrandVo brandVo = new BrandVo();
            //brandId
            brandVo.setBrandId(Long.parseLong(bucket.getKeyAsString()));
            //brandImg
            ParsedStringTerms brandImg = bucket.getAggregations().get("brandImg");
            brandVo.setBrandImg(brandImg.getBuckets().get(0).getKeyAsString());
            //brandName
            ParsedStringTerms brandName = bucket.getAggregations().get("brandName");
            brandVo.setBrandName(brandName.getBuckets().get(0).getKeyAsString());
            brandList.add(brandVo);
        }
        result.setBrands(brandList);

        //当前查询到的结果,所有涉及的分类
        List<CatalogVo> catalogList = new ArrayList<>();
        ParsedLongTerms catalogAgg = aggregations.get("catalogAgg");
        for (Terms.Bucket bucket : catalogAgg.getBuckets()) {
            CatalogVo catalogVo = new CatalogVo();
            String catalogId = bucket.getKeyAsString();
            //catalogId
            catalogVo.setCatalogId(Long.parseLong(catalogId));
            //catalogName
            ParsedStringTerms catalogName = bucket.getAggregations().get("catalogName");
            catalogVo.setCatalogName(catalogName.getBuckets().get(0).getKeyAsString());
            catalogList.add(catalogVo);
        }
        result.setCatalogs(catalogList);

        //当前查询到的结果,所有涉及的属性
        List<AttrVo> attrList = new ArrayList<>();
        ParsedNested attrsAgg = aggregations.get("attrsAgg");
        ParsedLongTerms attrIdAgg = attrsAgg.getAggregations().get("attrIdAgg");
        for (Terms.Bucket bucket : attrIdAgg.getBuckets()) {
            AttrVo attrVo = new AttrVo();
            //attrId
            attrVo.setAttrId(bucket.getKeyAsNumber().longValue());
            //attrName
            ParsedStringTerms attrsName = bucket.getAggregations().get("attrsName");
            attrVo.setAttrName(attrsName.getBuckets().get(0).getKeyAsString());
            //attr
            ParsedStringTerms attrsValue = bucket.getAggregations().get("attrsValue");
            List<String> attrValue = attrsValue.getBuckets().stream().map(item -> {
                return ((Terms.Bucket) item).getKeyAsString();
            }).collect(Collectors.toList());
            attrVo.setAttrValue(attrValue);
            attrList.add(attrVo);
        }
        result.setAttrs(attrList);

        /* 分页信息 */
        //当前页码
        result.setPageNum(params.getPageNum());
        //总记录数
        long total = hits.getTotalHits().value;
        result.setTotal(total);

        //总页码数  总记录数%分页数
        long totalPage = total%Esconstant.PRODUCT_PAGENUMBER == 0?total/Esconstant.PRODUCT_PAGENUMBER:(total/Esconstant.PRODUCT_PAGENUMBER+1);
        result.setTotalPages((int) totalPage);
        List<Integer> totalPagesList = new ArrayList<>();
        if (result.getTotalPages() != null && result.getTotalPages() !=0){
            for (int i = 0; i < result.getTotalPages(); i++) {
                totalPagesList.add(i);
            }
        }
        result.setTotalPagesList(totalPagesList);
        return result;
    }

    /**
     * 准备检索需求
     * @param params
     * @return
     */
    private SearchRequest buildSearchRequest(SearchParams params) {
        SearchSourceBuilder searchSource = SearchSourceBuilder.searchSource();
        /**
         * 模糊匹配,过滤(属性,分类,品牌,价格区间,库存)
         */
        //构建bool-query
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        //1.1 must 模糊匹配
        if (StringUtils.isNotBlank(params.getKeyword())){
            queryBuilder.must(QueryBuilders.matchQuery("skuTitle",params.getKeyword()));
        }
        //1.2 filter 过滤
        //1.2.1 分类
        if (params.getCatalog3Id() != null){
            queryBuilder.filter(QueryBuilders.termQuery("catalogId",params.getCatalog3Id()));
        }
        //1.2.2  品牌
        if (params.getBrandId()!= null && params.getBrandId().size() >0){
            queryBuilder.filter(QueryBuilders.termsQuery("brandId",params.getBrandId()));
        }
        //1.2.3 nested 属性 attrs=1_5寸:6寸&attrs=2_16G:8G
        if (params.getAttrs() != null && params.getAttrs().size()>0){
            params.getAttrs().forEach(res->{
                BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
                //1_5寸:6寸
                String[] attr = res.split("_");
                String attrId = attr[0]; //属性id
                String[] attrValue = attr[1].split(":");
                boolQuery.filter(QueryBuilders.termsQuery("attrs.attrId",attrId));
                boolQuery.filter(QueryBuilders.termsQuery("attrs.attrValue",attrValue));
                NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery("attrs", boolQuery, ScoreMode.None);
                queryBuilder.filter(nestedQuery);
            });
        }

        //1.2.4  是否有库存 0/1
        if (params.getHasStock() != null){
            queryBuilder.filter(QueryBuilders.termQuery("hasStock",params.getHasStock()==1));
        }

        //1.2.5 价格区间 skuPrice=1_500/_500/500_
        if (StringUtils.isNotBlank(params.getSkuPrice())){
            RangeQueryBuilder rangePrice = QueryBuilders.rangeQuery("skuPrice");
            String[] price = params.getSkuPrice().split("_");
            if (price.length > 1 && !"".equals(price[0]) && !"".equals(price[1])){
                //1_500
                rangePrice.gte(price[0]).lte(price[1]);
            }
            if (params.getSkuPrice().startsWith("_")){
                //_500
                rangePrice.lte(price[1]);
            }else {
                //500_
                rangePrice.gte(price[0]);
            }
            queryBuilder.filter(rangePrice);
        }

        //聚合分析
        //品牌聚合
        TermsAggregationBuilder brandAgg = AggregationBuilders.terms("brandAgg");
        brandAgg.field("brandId").size(20);
        //品牌聚合 - 子聚合 品牌名称
        TermsAggregationBuilder brandName = AggregationBuilders.terms("brandName");
        brandName.field("brandName").size(10);
        brandAgg.subAggregation(brandName);
        //品牌聚合 - 子聚合 品牌图片
        TermsAggregationBuilder brandImg = AggregationBuilders.terms("brandImg");
        brandImg.field("brandImg").size(10);
        brandAgg.subAggregation(brandImg);
        searchSource.aggregation(brandAgg);

        //分类聚合
        TermsAggregationBuilder catalogAgg = AggregationBuilders.terms("catalogAgg");
        catalogAgg.field("catalogId").size(20);
        //分类聚合 - 子聚合 分类名称
        TermsAggregationBuilder catalogName = AggregationBuilders.terms("catalogName");
        catalogName.field("catalogName").size(20);
        catalogAgg.subAggregation(catalogName);
        searchSource.aggregation(catalogAgg);

        //属性聚合 nested
        NestedAggregationBuilder nested = AggregationBuilders.nested("attrsAgg", "attrs");
        //属性id
        TermsAggregationBuilder attrIdAgg = AggregationBuilders.terms("attrIdAgg");
        attrIdAgg.field("attrs.attrId").size(10);
        //属性id - 子聚合 属性名称
        TermsAggregationBuilder attrsName = AggregationBuilders.terms("attrsName");
        attrsName.field("attrs.attrName").size(10);
        //属性id - 子聚合 属性值
        TermsAggregationBuilder attrsValue = AggregationBuilders.terms("attrsValue");
        attrsValue.field("attrs.attrValue").size(10);

        attrIdAgg.subAggregation(attrsValue);
        attrIdAgg.subAggregation(attrsName);
        nested.subAggregation(attrIdAgg);
        searchSource.aggregation(nested);
        searchSource.query(queryBuilder);


        /**
         * 排序,分页,高亮
          */
        //2.1 排序
        //sort=hotScore_asc/desc 热度排序
        if (StringUtils.isNotBlank(params.getSort())){
            String[] sorts = params.getSort().split("_");
            //排序条件
            String sortName = sorts[0];
            SortOrder sortOrder = sorts[1].equalsIgnoreCase("asc")?SortOrder.ASC:SortOrder.DESC;
            searchSource.sort(sortName,sortOrder);
        }

        //2.2 分页  pageSize:5
        /**
         *  pageNum:1 from:0 size:5 [0,1,2,3,4]
         *  pageNum:2 from:5 size:5 [5,6,7,8,9]
         *  pageNum:n from:(n-1)*size
         */
        searchSource.from((params.getPageNum()-1)*Esconstant.PRODUCT_PAGENUMBER);
        searchSource.size(Esconstant.PRODUCT_PAGENUMBER);

        //2.3 高亮
        if (StringUtils.isNotBlank(params.getKeyword())){
            HighlightBuilder builder = new HighlightBuilder();
            builder.field("skuTitle");
            builder.preTags("<b style='color:red'>");
            builder.postTags("<b/>");
            searchSource.highlighter(builder);
        }

        String s = searchSource.toString();
        System.out.println("DSL语句:"+s);


        /**
         * 聚合分析
         */
         return new SearchRequest(new String[]{Esconstant.PRODUCT_INDEXT}, searchSource);
    }
}
