package com.zy.webmail.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.zy.webmail.product.entity.AttrAttrgroupRelationEntity;
import com.zy.webmail.product.entity.ProductAttrValueEntity;
import com.zy.webmail.product.service.ProductAttrValueService;
import com.zy.webmail.product.vo.AttrRespVo;
import com.zy.webmail.product.vo.AttrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.zy.webmail.product.entity.AttrEntity;
import com.zy.webmail.product.service.AttrService;
import com.zy.common.utils.PageUtils;
import com.zy.common.utils.R;



/**
 * 商品属性
 *
 * @author zhuoyu
 * @email 787958123@qq.com
 * @date 2020-04-18 12:58:00
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;

    @Autowired
    private ProductAttrValueService productAttrValueService;
    //product/attr/base/list/{catelogId}
    ///product/attr/sale/list/{catelogId}
    /**
     * 列表
     */
    @GetMapping("/{attrtype}/list/{catelogId}")
    public R baseAttrList(@RequestParam Map<String, Object> params,
                          @PathVariable("catelogId") Long catelogId,
                          @PathVariable("attrtype")String attrtype){
        PageUtils page = attrService.querybaseAttrPage(params,catelogId,attrtype);
        return R.ok().put("page", page);
    }

    ///product/attr/base/listforspu/{spuId}
    /*
    *      获取spu规格
    * */
    @GetMapping("/base/listforspu/{spuId}")
    public R listforspu(@PathVariable("spuId") Long spuId){
//        PageUtils page = attrService.querybaseAttrPage();
        List<ProductAttrValueEntity>  entities=attrService.listforspu(spuId);
        return R.ok().put("data", entities);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:attr:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = attrService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
    //@RequiresPermissions("product:attr:info")
    public R info(@PathVariable("attrId") Long attrId){
//		AttrEntity attr = attrService.getById(attrId);  //返回普通attr属性，不包括catelogPath和attrGroupId

		AttrRespVo attrRespVo=attrService.getDetail(attrId);  //添加全路径属性以及分组id属性，包括catelogPath和attrGroupId
        return R.ok().put("attr", attrRespVo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:attr:save")
    public R save(@RequestBody AttrVo attr){
		attrService.saveAttr(attr);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:attr:update")
    public R update(@RequestBody AttrRespVo attr){
//		attrService.updateById(attr);
        attrService.updateAttr(attr);
        return R.ok();
    }

    //product/attr/update/{spuId}
    /*
    *  修改商品规格
    * */
    @PostMapping("/update/{spuId}")
    //@RequiresPermissions("product:attr:update")
    public R updateSpu(@PathVariable("spuId") Long spuId,@RequestBody List<ProductAttrValueEntity> productAttrValueEntities){
        productAttrValueService.updateSpu(spuId,productAttrValueEntities);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:attr:delete")
    public R delete(@RequestBody Long[] attrIds){
		attrService.removeByIds(Arrays.asList(attrIds));

        return R.ok();
    }

}
