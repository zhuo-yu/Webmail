package com.zy.webmail.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zy.common.utils.PageUtils;
import com.zy.webmail.product.entity.CategoryBrandRelationEntity;

import java.util.List;
import java.util.Map;

/**
 * 品牌分类关联
 *
 * @author zhuoyu
 * @email 787958123@qq.com
 * @date 2020-04-18 11:53:40
 */
public interface CategoryBrandRelationService extends IService<CategoryBrandRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<CategoryBrandRelationEntity> getcateloglist(Long BrandId);

    void saveDetail(CategoryBrandRelationEntity categoryBrandRelation);

    void updatebrand(Long brandId, String name);

    void updatecategory(Long catId, String name);

    List<CategoryBrandRelationEntity> getbrands(Long catId);
}

