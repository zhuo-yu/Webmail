package com.zy.webmail.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.zy.webmail.product.dao.AttrAttrgroupRelationDao;
import com.zy.webmail.product.entity.AttrEntity;
import com.zy.webmail.product.service.AttrAttrgroupRelationService;
import com.zy.webmail.product.service.AttrService;
import com.zy.webmail.product.service.CategoryService;
import com.zy.webmail.product.service.impl.AttrAttrgroupRelationServiceImpl;
import com.zy.webmail.product.vo.AttrGroupRelationVo;
import com.zy.webmail.product.vo.AttrGroupWithAttrsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.zy.webmail.product.entity.AttrGroupEntity;
import com.zy.webmail.product.service.AttrGroupService;
import com.zy.common.utils.PageUtils;
import com.zy.common.utils.R;

import javax.websocket.server.PathParam;


/**
 * 属性分组
 *
 * @author zhuoyu
 * @email 787958123@qq.com
 * @date 2020-04-18 12:58:00
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AttrService attrService;

    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;
    ///product/attrgroup/{attrgroupId}/attr/relation   添加属性与分组关联关系

    /**
     * 获取属性组关联
     */
    @RequestMapping("/{attrgroupId}/attr/relation")
    public R attrgrouprelation(@RequestParam Map<String, Object> params,@PathVariable("attrgroupId") Long attrgroupId){
            List<AttrEntity> attrEntity=attrService.getRelationAttr(attrgroupId);
            return R.ok().put("data", attrEntity);
    }

    ///product/attrgroup/attr/relation
    /*
    * 添加属性与分组关联关系
    * */
    @PostMapping("/attr/relation")
    public R addRelation(@RequestBody AttrGroupRelationVo[] relationVo){
        attrAttrgroupRelationService.saveBatch(relationVo);
        return R.ok();
    }
    ///product/attrgroup/{attrgroupId}/noattr/relation 获取属性分组没有关联的其他属性
    /**
     * 获取属性分组没有关联的其他属性
     */
    @RequestMapping("/{attrgroupId}/noattr/relation")
    public R attrgroupnorelation(@RequestParam Map<String, Object> params,@PathVariable("attrgroupId") Long attrgroupId){
            PageUtils pageUtils = attrService.getNoRelationAttr(params,attrgroupId);
            return R.ok().put("data", pageUtils);

    }
    ///product/attrgroup/{catelogId}/withattr
    /*
    * 获取分类下所有分组&关联属性
    * */
    @GetMapping("/{catelogId}/withattr")
    public R getAttGroupwithAttrs(@PathVariable("catelogId") Long catelogId){
        //1、查出当前分类下的所有属性分组
        //2、查出每个属性分组的所有属性
        List<AttrGroupWithAttrsVo> voList=attrGroupService.getAttrGroupWithAttrsByCatelogId(catelogId);
        return R.ok().put("data",voList);
    }
    /**
     * 列表
     */
    @RequestMapping("/list/{catelogId}")
    public R list(@RequestParam Map<String, Object> params,@PathVariable("catelogId") Long catelogId){
//        PageUtils page = attrGroupService.queryPage(params);
        PageUtils page=attrGroupService.queryPage(params,catelogId);
        return R.ok().put("page", page);
    }

    //  删除关联关系
    // /product/attrgroup/attr/relation/delete
    @PostMapping("/attr/relation/delete")
    public R deleteRelation(@RequestBody AttrGroupRelationVo[] vos){
        attrService.deleteRelation(vos);
        return R.ok();
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    //@RequiresPermissions("product:attrgroup:info")
    public R info(@PathVariable("attrGroupId") Long attrGroupId){
		AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);
        Long catelogId = attrGroup.getCatelogId();
        Long[] paths=categoryService.getcatelogpath(catelogId); //返回全路径数组
        attrGroup.setCatelogPath(paths); //设置全路径
        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:attrgroup:save")
    public R save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:attrgroup:update")
    public R update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:attrgroup:delete")
    public R delete(@RequestBody Long[] attrGroupIds){
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

}
