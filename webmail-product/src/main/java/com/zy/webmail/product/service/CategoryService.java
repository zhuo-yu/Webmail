package com.zy.webmail.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zy.common.utils.PageUtils;
import com.zy.webmail.product.entity.CategoryEntity;
import com.zy.webmail.product.vo.Catagory2Vo;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author zhuoyu
 * @email 787958123@qq.com
 * @date 2020-04-18 11:53:40
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    public List<CategoryEntity> listWithTree(); //查出所有的菜单目录数据，并将父子菜单组装起来

    void removeMenusByIds(List<Long> asList);  //逻辑删除

    Long[] getcatelogpath(Long attrGroupId);  //寻找catelogId全路径

    void updateDetail(CategoryEntity category);

    List<CategoryEntity> getLevelOne();

    Map<String, List<Catagory2Vo>> getCatagoryJSON();
}

