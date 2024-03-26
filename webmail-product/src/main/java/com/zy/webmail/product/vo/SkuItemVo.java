package com.zy.webmail.product.vo;

import com.zy.webmail.product.entity.SkuImagesEntity;
import com.zy.webmail.product.entity.SkuInfoEntity;
import com.zy.webmail.product.entity.SpuInfoDescEntity;
import lombok.Data;

import java.util.List;

@Data
public class SkuItemVo {
    //1、sku基本信息 pms_sku_info
    SkuInfoEntity info;

    Boolean hasStock = true;

    //2、sku图片信息 pms_spu_images
    List<SkuImagesEntity> images;

    //3、获取spu的销售属性组合
    List<SkuItemSaleAttrVo> saleAttr;

    //4、获取spu的介绍
    SpuInfoDescEntity desp;

    //5、获取spu的规格参数信息
    List<SpuItemAttrGroupVo> groupAttrs;

}
