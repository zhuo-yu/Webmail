package com.zy.webmail.product.vo;

import lombok.Data;
import lombok.ToString;

/**
 * spu基本规格属性信息
 */
@Data
@ToString
public class SpuBaseAttrVo {
    private String attrName;
    private String attrValue;

    public String getAttrName() {
        return attrName;
    }

    public void setAttrName(String attrName) {
        this.attrName = attrName;
    }

    public String getAttrValue() {
        return attrValue;
    }

    public void setAttrValue(String attrValue) {
        this.attrValue = attrValue;
    }
}
