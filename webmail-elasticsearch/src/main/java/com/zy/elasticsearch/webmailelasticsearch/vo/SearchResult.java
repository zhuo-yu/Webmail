package com.zy.elasticsearch.webmailelasticsearch.vo;

import com.zy.common.to.es.SkuEsModel;
import lombok.Data;

import java.util.List;

/**
 * 查询返回数据
 */
@Data
public class SearchResult {

    //查询到的所有商品信息
    private List<SkuEsModel> products;

    /**
     * 分页信息
     */
    private Integer pageNum; //当前页码
    private Long total; //总记录数
    private Integer totalPages; //总页码数
    private List<Integer> totalPagesList;//总页码集合

    private List<BrandVo> brands; //当前查询到的结果,所有涉及的品牌
    private List<CatalogVo> catalogs; //当前查询到的结果,所有涉及的分类
    private List<AttrVo> attrs; //当前查询到的结果,所有涉及的属性


}
