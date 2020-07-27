package com.zy.webmail.product.service.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zy.common.utils.PageUtils;
import com.zy.common.utils.Query;

import com.zy.webmail.product.dao.SkuInfoDao;
import com.zy.webmail.product.entity.SkuInfoEntity;
import com.zy.webmail.product.service.SkuInfoService;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    /*保存sku基本信息*/
    @Override
    public void saveSkuInfo(SkuInfoEntity skuInfoEntity) {
        this.baseMapper.insert(skuInfoEntity);
    }

    /*检索sku详细信息*/
    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SkuInfoEntity> wrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            wrapper.and(item->{
                item.eq("sku_id",key).or().like("sku_name",key);
            });
        }
        String catalogId = (String) params.get("catelogId");
        if(!StringUtils.isEmpty(catalogId) && !"0".equalsIgnoreCase(catalogId)){
            wrapper.and(item->{
                item.eq("catalog_id",catalogId);
            });
        }
        String brandId = (String) params.get("brandId");
        if(!StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(brandId)){
            wrapper.and(item->{
                item.eq("brand_id",brandId);
            });
        }
        String min = (String) params.get("min");
        if(!StringUtils.isEmpty(min)){
            wrapper.and(item->{
                item.ge("price",min); //大于等于
            });
        }
        String max = (String) params.get("max");
        if(!StringUtils.isEmpty(max)){
            try {
                BigDecimal decimal = new BigDecimal(max);
                if(decimal.compareTo(new BigDecimal("0"))==1){
                    wrapper.and(item->{
                        item.le("price",max); //小于等于
                    });
                }
            }catch (Exception e){

            }

        }
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                wrapper
        );
        PageUtils pageUtils = new PageUtils(page);
        return pageUtils;
    }

    @Override
    public List<SkuInfoEntity> getSkuIdBySpuId(Long spuId) {
        List<SkuInfoEntity> skulist = this.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));
        return skulist;
    }

}