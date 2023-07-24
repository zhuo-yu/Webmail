package com.zy.elasticsearch.webmailelasticsearch.vo;

import lombok.Data;

import java.util.List;

/**
 * 查询返回结果属性信息
 */
@Data
public class AttrVo {
    private Long attrId;
    private String attrName;
    private List<String> attrValue;
}
