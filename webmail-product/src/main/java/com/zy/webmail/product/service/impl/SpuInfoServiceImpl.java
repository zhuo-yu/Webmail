package com.zy.webmail.product.service.impl;

import com.zy.common.constant.productconstant;
import com.zy.common.to.SkuHasStockVo;
import com.zy.common.to.SkuReductionTo;
import com.zy.common.to.SpuBoundTo;
import com.zy.common.to.es.SkuEsModel;
import com.zy.common.utils.R;
import com.zy.webmail.product.dao.SpuInfoDescDao;
import com.zy.webmail.product.entity.*;
import com.zy.webmail.product.feign.CouponFeignService;
import com.zy.webmail.product.feign.SearchFeignService;
import com.zy.webmail.product.feign.WareFeignService;
import com.zy.webmail.product.service.*;
import com.zy.webmail.product.vo.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zy.common.utils.PageUtils;
import com.zy.common.utils.Query;

import com.zy.webmail.product.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Resource
    SpuInfoDescDao spuInfoDescDao;

    @Autowired
    SpuInfoDescService spuInfoDescService;

    @Autowired
    SpuImagesService spuImagesService;

    @Autowired
    AttrService attrService;

    @Autowired
    ProductAttrValueService productAttrValueService;

    @Autowired
    SkuInfoService skuInfoService;

    @Autowired
    SkuImagesService skuImagesService;

    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    CouponFeignService couponFeignService;

    @Autowired
    BrandService brandService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    WareFeignService wareFeignService;

    @Autowired
    SearchFeignService searchFeignService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }


    /*
    * 失败逻辑  TODO 待完善
    * */
    @Transactional
    @Override
    public void spuSaveInfo(SpuSaveVo saveVo) {
        //1、保存spu基本信息 pms_spu_info
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(saveVo,spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        this.saveBaseSpuInfo(spuInfoEntity);
        //2、保存spu的描述图片  `pms_spu_info_desc`
        List<String> decript = saveVo.getDecript();
        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        spuInfoDescEntity.setSpuId(spuInfoEntity.getId());
        spuInfoDescEntity.setDecript(String.join(",",decript));
        spuInfoDescService.saveSpuInfoDesc(spuInfoDescEntity);
        //3、保存spu的图片集  `pms_spu_images`
        List<String> images = saveVo.getImages();
        spuImagesService.saveImages(spuInfoEntity.getId(),images);
        //4、保存spu的规格参数 `pms_product_attr_value`
        List<BaseAttrs> baseAttrs = saveVo.getBaseAttrs();
        List<ProductAttrValueEntity> collect = baseAttrs.stream().map(item -> {
            ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
            productAttrValueEntity.setAttrId(item.getAttrId());
            AttrEntity byId = attrService.getById(item.getAttrId()); //获取attr冗余名字
            productAttrValueEntity.setAttrName(byId.getAttrName());
            productAttrValueEntity.setQuickShow(item.getShowDesc());
            productAttrValueEntity.setSpuId(spuInfoEntity.getId());
            productAttrValueEntity.setAttrValue(item.getAttrValues());
            return productAttrValueEntity;
        }).collect(Collectors.toList());
        productAttrValueService.saveProductAttr(collect);
        //5、保存当前spu对应的所有sku信息
        /*
        * 	private String skuName;
        *   private BigDecimal price;
        *   private String skuTitle;
            private String skuSubtitle;
        * */
        List<Skus> skus = saveVo.getSkus();
        if(skus!=null &&skus.size()!=0){
            skus.forEach(item->{
                //5.1）、保存sku的基本信息  `pms_sku_info`
                String defaultImage="";
                for (Images img: item.getImages()
                ) {
                    if(img.getDefaultImg()==1){
                        defaultImage=img.getImgUrl();
                    }
                }
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(item,skuInfoEntity);  //赋值上述四个属性
                skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
                skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
                skuInfoEntity.setSaleCount(0L);
                skuInfoEntity.setSpuId(spuInfoEntity.getId());
                skuInfoEntity.setSkuDefaultImg(defaultImage);
                skuInfoService.saveSkuInfo(skuInfoEntity);  //save 保存后自增id，出现skuid

                //5.2）、保存sku的图片信息  `pms_sku_images`
                Long skuId = skuInfoEntity.getSkuId();
                List<SkuImagesEntity> imageslist = item.getImages().stream().map(img -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setImgUrl(img.getImgUrl());
                    skuImagesEntity.setDefaultImg(img.getDefaultImg());
                    return skuImagesEntity;
                }).filter(entity->{
                    return !StringUtils.isEmpty(entity.getImgUrl()); //返回有imgurl地址的
                }).collect(Collectors.toList());
                skuImagesService.saveImage(imageslist); //save  TODO 没有图片的路径无需保存

                //5.3）、sku的销售属性信息 `pms_sku_sale_attr_value`
                List<SkuSaleAttrValueEntity> SkuAttrs = item.getAttr().stream().map(attr -> {
                    SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(attr,skuSaleAttrValueEntity);
                    skuSaleAttrValueEntity.setSkuId(skuId);
                    return skuSaleAttrValueEntity;
                }).collect(Collectors.toList());
                skuSaleAttrValueService.saveSkuAttr(SkuAttrs); //save

                //5.4)、 sku的优惠、满减信息  webmail-sms->`sms_sku_full_reduction` and`sms_sku_ladder` and `sms_member_price`
                SkuReductionTo skuReductionTo = new SkuReductionTo();
                BeanUtils.copyProperties(item,skuReductionTo);
                skuReductionTo.setSkuId(skuId);
                if(skuReductionTo.getFullCount()>0 || skuReductionTo.getFullPrice().compareTo(new BigDecimal("0"))==1){
                    couponFeignService.saveSkuReduction(skuReductionTo);
                }


                //6、保存spu的积分信息   webmail-sms->`sms_spu_bounds`
                Bounds bounds = saveVo.getBounds();
                SpuBoundTo spuBoundTo = new SpuBoundTo();
                BeanUtils.copyProperties(bounds,spuBoundTo);
                spuBoundTo.setSpuId(spuInfoEntity.getId());
                couponFeignService.saveSpuBounds(spuBoundTo); //save
            });
        }





    }

    /*保存spu基本信息*/
    @Override
    public void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity) {
        this.baseMapper.insert(spuInfoEntity);
    }

    /*带条件的spu检索*/
    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();
        /*检索条件*/
        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            wrapper.and((item)->{
                item.eq("id",key).or().like("spu_name",key).or().like("spu_description",key);
            });
        }
        /*产品分类条件*/
        String catelogId = (String) params.get("catelogId");
        if(!StringUtils.isEmpty(catelogId)){
            wrapper.and(item->{
                item.eq("catalog_id",catelogId);
            });
        }
        /*产品品牌条件*/
        String brandId = (String) params.get("brandId");
        if(!StringUtils.isEmpty(brandId)){
            wrapper.and(item->{
                item.eq("brand_id",brandId);
            });
        }
        /*产品状态条件*/
        String status = (String) params.get("status");
        if(!StringUtils.isEmpty(status)){
            wrapper.and(item->{
                item.eq("publish_status",status);
            });
        }

        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                wrapper
        );
        PageUtils pageUtils = new PageUtils(page);
        return pageUtils;
    }

    /*商品上架*/
    @Transactional
    @Override
    public void up(Long spuId) {
        //通过spuid获取sku的信息
        List<SkuInfoEntity> skuInfoEntities=skuInfoService.getSkuIdBySpuId(spuId);
        List<Long> skuIdList = skuInfoEntities.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());
        //查询当前sku的所有可以被用来检索的规格属性
        List<ProductAttrValueEntity> list=productAttrValueService.baseAttrlistBySpuId(spuId);
        List<Long> AttrIds = list.stream().map(item -> {
            return item.getAttrId();
        }).collect(Collectors.toList());
        List<Long> attrIds= attrService.selectSearchAttrIds(AttrIds);  //通过attrid找出attr数据库中 search_type为1的数据
        Set<Long> idSet=new HashSet<>(attrIds);

        List<SkuEsModel.Attrs> AttrsList = list.stream().filter(item -> {
            return idSet.contains(item.getAttrId());
        }).map(item -> {
            SkuEsModel.Attrs attrs1 = new SkuEsModel.Attrs();
            BeanUtils.copyProperties(item, attrs1);
            return attrs1;
        }).collect(Collectors.toList());
        //TODO 发送远程调用，库存查询系统是否有库存
        Map<Long, Boolean> map=null;
        try{
            List<SkuHasStockVo> hasStock = wareFeignService.getSkusHasStock(skuIdList); //获得库存判断对象
            map= hasStock.stream().collect(Collectors.toMap(SkuHasStockVo::getSkuId, item -> item.isHasStock()));//收集成一个自定义的kv map,按照skuid对应库存判断的值组合成map
        }catch (Exception e){
            log.error("库存远程服务出现问题,原因{}",e);
        }
        //封装每个sku的信息
        Map<Long, Boolean> finalMap = map;
        List<SkuEsModel> skues = skuInfoEntities.stream().map(item -> {
            //1、组装需要的数据
            SkuEsModel skuEsModel = new SkuEsModel();
            BeanUtils.copyProperties(item,skuEsModel);

            //skuPrice  skuImg hasStock  hotScore brandName brandId catalogName List<Attrs> 需要单独处理
            skuEsModel.setSkuPrice(item.getPrice());   //skuPrice
            skuEsModel.setSkuImg(item.getSkuDefaultImg());  //skuImg
            //hasStock  hotScore

            //设置库存信息
            if(finalMap == null){
                skuEsModel.setHasStock(true); //设置上库存判断
            }else {
                skuEsModel.setHasStock(finalMap.get(item.getSkuId())); //设置上库存判断  hasStock
            }

            //TODO 热度评分 0
            skuEsModel.setHotScore(0L);   //hotScore
            //查询品牌和分类的信息
            BrandEntity brandEntity = brandService.getById(item.getBrandId());
            skuEsModel.setBrandId(brandEntity.getBrandId());  //brandId
            skuEsModel.setBrandImg(brandEntity.getLogo());   //brandImg
            skuEsModel.setBrandName(brandEntity.getName());  //brandName

            CategoryEntity categoryEntity = categoryService.getById(item.getCatalogId());
            skuEsModel.setCatalogName(categoryEntity.getName());   //catalogName

            //设置检索属性
            skuEsModel.setAttrs(AttrsList);  //SkuEsModel.Attrs


            return skuEsModel;
        }).collect(Collectors.toList());

        //将数据发送给es进行保存 webmail-elasticsearch
        R r = searchFeignService.productStatusUp(skues);
        if(r.get("code").equals(0)){
            //远程调用成功
            //TODO 6、改掉当前spu的状态
            baseMapper.updateSpuStatus(spuId, productconstant.StatusEnum.SPU_UP.getCode());
        }else{
            //远程调用失败
            //TODO 7、重复调用问题
        }
    }


}