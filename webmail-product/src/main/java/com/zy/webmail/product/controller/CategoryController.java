package com.zy.webmail.product.controller;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zy.webmail.product.entity.CategoryEntity;
import com.zy.webmail.product.service.CategoryService;
import com.zy.common.utils.PageUtils;
import com.zy.common.utils.R;



/**
 * 商品三级分类
 *
 * @author zhuoyu
 * @email 787958123@qq.com
 * @date 2020-04-18 12:58:00
 */
@RestController
@RequestMapping("/product/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 查出所有分类以及子分类、以树形结构组装起来
     */
    @RequestMapping(value = "/list/tree")
    public R list(@RequestParam Map<String, Object> params){
        List<CategoryEntity> categoryEntities = categoryService.listWithTree(); //查出所有的分类
        return R.ok().put("data", categoryEntities);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{catId}")
    //@RequiresPermissions("product:category:info")
    public R info(@PathVariable("catId") Long catId){
		CategoryEntity category = categoryService.getById(catId);
        return R.ok().put("data", category);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:category:save")
    public R save(@RequestBody CategoryEntity category){
		categoryService.save(category);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:category:update")
    public R update(@RequestBody CategoryEntity category){
		categoryService.updateDetail(category);  //需要同步修改其他关联表

        return R.ok();
    }

    /**
     * 删除,使用逻辑删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:category:delete")
    public R delete(@RequestBody Long[] catIds){
        //检查当前删除的菜单是否被别的地方引用
//		categoryService.removeByIds(Arrays.asList(catIds));\
        System.out.println(catIds);
        categoryService.removeMenusByIds(Arrays.asList(catIds));
        return R.ok();
    }

}