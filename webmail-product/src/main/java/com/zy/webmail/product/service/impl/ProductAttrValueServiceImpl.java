package com.zy.webmail.product.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zy.common.utils.PageUtils;
import com.zy.common.utils.Query;

import com.zy.webmail.product.dao.ProductAttrValueDao;
import com.zy.webmail.product.entity.ProductAttrValueEntity;
import com.zy.webmail.product.service.ProductAttrValueService;
import org.springframework.transaction.annotation.Transactional;


@Service("productAttrValueService")
public class ProductAttrValueServiceImpl extends ServiceImpl<ProductAttrValueDao, ProductAttrValueEntity> implements ProductAttrValueService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ProductAttrValueEntity> page = this.page(
                new Query<ProductAttrValueEntity>().getPage(params),
                new QueryWrapper<ProductAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    /*保存spu的规格参数*/
    @Override
    public void saveProductAttr(List<ProductAttrValueEntity> collect) {
        this.saveBatch(collect);
    }

    /*修改商品规格*/
    @Transactional
    @Override
    public void updateSpu(Long spuId, List<ProductAttrValueEntity> productAttrValueEntities) {
        //删除spuid之前对应的所有属性
        this.baseMapper.delete(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id",spuId));
        //插入更改后的数据
        List<ProductAttrValueEntity> collect = productAttrValueEntities.stream().map(item -> {
            ProductAttrValueEntity entity = new ProductAttrValueEntity();
            BeanUtils.copyProperties(item, entity);
            entity.setSpuId(spuId);
            return entity;
        }).collect(Collectors.toList());
        this.saveBatch(collect);
    }

    @Override
    public List<ProductAttrValueEntity> baseAttrlistBySpuId(Long spuId) {
        List<ProductAttrValueEntity> entities = this.baseMapper.selectList(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId));
        return entities;
    }

}