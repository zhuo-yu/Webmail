package com.zy.elasticsearch.webmailelasticsearch.vo;

import lombok.Data;

/**
 * 查询返回结果品牌信息
 */
@Data
public class BrandVo {
    private Long brandId;
    private String brandName;
    private String brandImg;
}
