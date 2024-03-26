package com.zy.webmail.product.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * spu规格参数
 */
@Data
@ToString
public class SpuItemAttrGroupVo {
    private String groupName;

    private List<SpuBaseAttrVo> attrs;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<SpuBaseAttrVo> getAttrs() {
        return attrs;
    }

    public void setAttrs(List<SpuBaseAttrVo> attrs) {
        this.attrs = attrs;
    }
}
