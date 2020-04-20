package com.zy.webmail.product.dao;

import com.zy.webmail.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author zhuoyu
 * @email 787958123@qq.com
 * @date 2020-04-18 11:53:40
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
