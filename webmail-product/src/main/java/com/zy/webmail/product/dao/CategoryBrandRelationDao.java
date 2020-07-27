package com.zy.webmail.product.dao;

import com.zy.webmail.product.entity.CategoryBrandRelationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 品牌分类关联
 * 
 * @author zhuoyu
 * @email 787958123@qq.com
 * @date 2020-04-18 11:53:40
 */
@Mapper
public interface CategoryBrandRelationDao extends BaseMapper<CategoryBrandRelationEntity> {
    @Select("select *from pms_category_brand_relation where catelog_id=#{catelogId}")
    public CategoryBrandRelationEntity selectBycategoryid(@Param("catelogId")Long catelogId);

//    public void updatebrand(CategoryBrandRelationEntity categoryBrandRelationEntity);
}
