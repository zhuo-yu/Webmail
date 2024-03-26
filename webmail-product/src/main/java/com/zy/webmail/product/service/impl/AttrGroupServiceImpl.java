package com.zy.webmail.product.service.impl;

import com.zy.webmail.product.dao.AttrAttrgroupRelationDao;
import com.zy.webmail.product.entity.AttrAttrgroupRelationEntity;
import com.zy.webmail.product.entity.AttrEntity;
import com.zy.webmail.product.service.AttrService;
import com.zy.webmail.product.vo.AttrGroupRelationVo;
import com.zy.webmail.product.vo.AttrGroupWithAttrsVo;
import com.zy.webmail.product.vo.SpuItemAttrGroupVo;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zy.common.utils.PageUtils;
import com.zy.common.utils.Query;

import com.zy.webmail.product.dao.AttrGroupDao;
import com.zy.webmail.product.entity.AttrGroupEntity;
import com.zy.webmail.product.service.AttrGroupService;

import javax.annotation.Resource;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Resource
    AttrGroupDao attrGroupDao;

    @Resource
    AttrAttrgroupRelationDao attrAttrgroupRelationDao;

    @Autowired
    AttrService attrService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }
    /*带有catelogId的分页查询*/
    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogId) {
        if(catelogId==0){
            IPage<AttrGroupEntity> page = this.page(
                    new Query<AttrGroupEntity>().getPage(params),
                    new QueryWrapper<AttrGroupEntity>()
            );
            return new PageUtils(page);
        }else {
            //select *from pms_attr_group catelog_id where  = ? and (attr_group_id=key or attr_group_name like %key% )
            String key = (String) params.get("key");
            System.out.println("key值为:"+key);
            QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<AttrGroupEntity>().eq("catelog_id",catelogId); //只通过categoryId查询
            if(!StringUtils.isEmpty(key)){   //如果查询条件带有检索key,则加上检索的条件
                wrapper.and((obj)->{
                    obj.eq("attr_group_id",key).or().like("attr_group_name",key);
                });
            }
            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params),wrapper);
            return new PageUtils(page);
        }

    }

    /*获取分类下所有分组&关联属性*/
    @Override
    public List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsByCatelogId(Long catelogId) {
        //1、查询属性分组
        List<AttrGroupEntity> attrGroupEntities = attrGroupDao.selectList(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
        List<AttrGroupWithAttrsVo> collect = attrGroupEntities.stream().map(item -> {   //将属性分组pojo赋值给vo返回成集合
            AttrGroupWithAttrsVo attrGroupWithAttrsVo = new AttrGroupWithAttrsVo();
            List<AttrEntity> relationAttr = attrService.getRelationAttr(item.getAttrGroupId()); //查出与属性分组关联的属性
            BeanUtils.copyProperties(item, attrGroupWithAttrsVo);
            attrGroupWithAttrsVo.setAttrs(relationAttr);
            return attrGroupWithAttrsVo;
        }).collect(Collectors.toList());
        //2、查询属性分组的全部属性

        return collect;
    }

    /**
     * 获取spu的规格参数信息
     * @param spuId
     * @return
     */
    @Override
    public List<SpuItemAttrGroupVo> getAttrGroupWithAttrsBySpuId(Long spuId) {
        //1、查出spu对应的分组名及下的分类信息
        return attrGroupDao.getAttrGroupWithAttrsBySpuId(spuId);
    }


}