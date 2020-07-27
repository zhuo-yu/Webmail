/**
  * Copyright 2020 bejson.com 
  */
package com.zy.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Auto-generated: 2020-05-21 21:30:56
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
public class MemberPrice {

    private Long id; //会员等级id
    private String name;//会员等级名
    private BigDecimal price; //会员对应价格

}