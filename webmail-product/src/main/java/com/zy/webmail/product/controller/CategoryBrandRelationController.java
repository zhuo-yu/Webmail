package com.zy.webmail.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zy.webmail.product.vo.BrandVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.zy.webmail.product.entity.CategoryBrandRelationEntity;
import com.zy.webmail.product.service.CategoryBrandRelationService;
import com.zy.common.utils.PageUtils;
import com.zy.common.utils.R;



/**
 * 品牌分类关联
 *
 * @author zhuoyu
 * @email 787958123@qq.com
 * @date 2020-04-18 12:58:00
 */
@RestController
@RequestMapping("/product/categorybrandrelation")
public class CategoryBrandRelationController {
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    /**
     * 获取当前品牌所有分类列表列表
     */
    @GetMapping("/catelog/list")
    public R cateloglist(@RequestParam("brandId") Long brandId){
        List<CategoryBrandRelationEntity> list= categoryBrandRelationService.getcateloglist(brandId);
//        List<CategoryBrandRelationEntity> catalog_id = categoryBrandRelationService.list(new QueryWrapper<CategoryBrandRelationEntity>().eq("catalog_id", BrandId));
        return R.ok().put("data", list);
    }

    /*
    * /product/categorybrandrelation/brands/list
    * 获取分类关联的品牌
    * */
    @GetMapping("/brands/list")
    public R getbrands(@RequestParam(value = "catId") Long catId){
        List<CategoryBrandRelationEntity> brandslist=categoryBrandRelationService.getbrands(catId);

        List<BrandVo> collect = brandslist.stream().map((item) -> {
            BrandVo brandVo = new BrandVo();
            brandVo.setBrandName(item.getBrandName());
            brandVo.setBrandId(item.getBrandId());
            return brandVo;
        }).collect(Collectors.toList());
        return R.ok().put("data",collect);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:categorybrandrelation:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = categoryBrandRelationService.queryPage(params);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("product:categorybrandrelation:info")
    public R info(@PathVariable("id") Long id){
		CategoryBrandRelationEntity categoryBrandRelation = categoryBrandRelationService.getById(id);

        return R.ok().put("categoryBrandRelation", categoryBrandRelation);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:categorybrandrelation:save")
    public R save(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
		categoryBrandRelationService.saveDetail(categoryBrandRelation);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:categorybrandrelation:update")
    public R update(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
		categoryBrandRelationService.updateById(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:categorybrandrelation:delete")
    public R delete(@RequestBody Long[] ids){
		categoryBrandRelationService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
