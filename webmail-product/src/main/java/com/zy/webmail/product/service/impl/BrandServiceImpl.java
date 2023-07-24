package com.zy.webmail.product.service.impl;

import com.zy.webmail.product.dao.CategoryBrandRelationDao;
import com.zy.webmail.product.entity.CategoryBrandRelationEntity;
import com.zy.webmail.product.service.CategoryBrandRelationService;
import lombok.experimental.Accessors;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zy.common.utils.PageUtils;
import com.zy.common.utils.Query;

import com.zy.webmail.product.dao.BrandDao;
import com.zy.webmail.product.entity.BrandEntity;
import com.zy.webmail.product.service.BrandService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {
//
//    @Resource
//    CategoryBrandRelationDao categoryBrandRelationDao;


    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        //1、获取模糊查询的key值
        String key = (String) params.get("key");
        if(StringUtils.isEmpty(key)){
            IPage<BrandEntity> page = this.page(
                    new Query<BrandEntity>().getPage(params),
                    new QueryWrapper<BrandEntity>()
            );
            return new PageUtils(page);
        }else{
            QueryWrapper<BrandEntity> wrapper=new QueryWrapper<BrandEntity>()
                    .eq("brand_id",key)
                    .or().like("name",key)
                    .or().like("descript",key);
            IPage<BrandEntity> page2 = this.page(
                    new Query<BrandEntity>().getPage(params),
                    wrapper
            );
            return new PageUtils(page2);
        }
    }

    @Transactional
    @Override
    public void updateDetail(BrandEntity brand) {
        //保证冗余字段的数据一致
        this.updateById(brand);
        if(!StringUtils.isEmpty(brand.getName())){
            //同步更新其他关联表的数据
            categoryBrandRelationService.updatebrand(brand.getBrandId(),brand.getName());
            //TODO 更新其他关联表
        }
    }


}