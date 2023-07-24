package com.zy.elasticsearch.webmailelasticsearch.vo;

import lombok.Data;

import java.util.List;

/**
 * 封装页面所有可能传递过来的查询条件
 * keyword=小米&catalog3Id=1&sort=saleCount_asc&hasStock=1&skuPrice=500_&brandId=1&brandId=2&attrs=1_5寸
 */
@Data
public class SearchParams {

    /**
     * 搜索栏关键字
     */
    private String keyword;

    /** 三级分类id */
    private Long catalog3Id;

    /**
     * 排序条件
     *  sort=saleCount_asc/desc 销量排序
     *  sort=skuPrice_asc/desc 价格排序
     *  sort=hotScore_asc/desc 热度排序
     */
    private String sort;

    /**
     * 筛选条件
     *  hasStock(是否有货),skuPrice(价格区间),brandId(品牌),attrs(sku属性)
     *  hasStock=0/1
     *  skuPrice=1_500/_500/500_
     *  brandId = 1
     *  attrs=1_5寸:6寸
     */
    private Integer hasStock = 1;//是否有货 0无库存,1有库存
    private String skuPrice;//价格区间
    private List<Long> brandId;// 品牌筛选,可多选
    private List<String> attrs;//属性刷选,可多选

    private Integer pageNum = 1;//页码
}
