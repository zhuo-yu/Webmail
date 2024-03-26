package com.zy.webmail.product.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * spu销售属性组合
 */
@Data
@ToString
public class SkuItemSaleAttrVo {
    private Long attrId;

    private String attrName;

    private String attrValues;
}
