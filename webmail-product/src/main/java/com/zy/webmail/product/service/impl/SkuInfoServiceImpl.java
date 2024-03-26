package com.zy.webmail.product.service.impl;

import com.zy.webmail.product.entity.SkuImagesEntity;
import com.zy.webmail.product.entity.SpuInfoDescEntity;
import com.zy.webmail.product.service.*;
import com.zy.webmail.product.vo.SkuItemSaleAttrVo;
import com.zy.webmail.product.vo.SkuItemVo;
import com.zy.webmail.product.vo.SpuItemAttrGroupVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zy.common.utils.PageUtils;
import com.zy.common.utils.Query;

import com.zy.webmail.product.dao.SkuInfoDao;
import com.zy.webmail.product.entity.SkuInfoEntity;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Autowired
    private SkuImagesService skuImagesService;

    @Autowired
    private SpuInfoDescService spuInfoDescService;

    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

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

    /**
     * 查询商品详情页面信息
     * @param skuId
     * @return
     */
    @Override
    public SkuItemVo item(Long skuId) throws ExecutionException, InterruptedException {
        SkuItemVo skuItemVo = new SkuItemVo();

        //使用异步编排,开启多线程执行
        CompletableFuture<SkuInfoEntity> infoFuture = CompletableFuture.supplyAsync(() -> {
            //1、sku基本信息 pms_sku_info
            SkuInfoEntity skuInfo = getById(skuId);
            skuItemVo.setInfo(skuInfo);
            return skuInfo;
        }, threadPoolExecutor);

        //待infoFuture执行完执行
        CompletableFuture<Void> saleAttrFuture = infoFuture.thenAcceptAsync(res -> {
            //3、获取spu的销售属性组合
            List<SkuItemSaleAttrVo> saleAttr = skuSaleAttrValueService.getSaleAttrsBySpuId(res.getSpuId());
            skuItemVo.setSaleAttr(saleAttr);
        }, threadPoolExecutor);

        //待infoFuture执行完执行
        CompletableFuture<Void> spuDescFuture = infoFuture.thenAcceptAsync(res -> {
            //4、获取spu的介绍
            SpuInfoDescEntity spuDesc = spuInfoDescService.getById(res.getSpuId());
            skuItemVo.setDesp(spuDesc);
        }, threadPoolExecutor);

        //待infoFuture执行完执行
        CompletableFuture<Void> groupAttrsFuture = infoFuture.thenAcceptAsync(res -> {
            //5、获取spu的规格参数信息
            List<SpuItemAttrGroupVo> groupAttrs = attrGroupService.getAttrGroupWithAttrsBySpuId(res.getSpuId());
            skuItemVo.setGroupAttrs(groupAttrs);
        }, threadPoolExecutor);

        //单独开启线程执行
        CompletableFuture<Void> imagesFuture = CompletableFuture.runAsync(() -> {
            //2、sku图片信息 pms_spu_images
            List<SkuImagesEntity> images = skuImagesService.getSkuImageBySkuId(skuId);
            skuItemVo.setImages(images);
        }, threadPoolExecutor);

        //阻塞,完成才往下执行
        CompletableFuture.allOf(saleAttrFuture,spuDescFuture,groupAttrsFuture,imagesFuture).get();
        return skuItemVo;
    }
}