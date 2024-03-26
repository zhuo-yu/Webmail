package com.zy.webmail.product.service.impl;

import com.zy.webmail.product.vo.SkuItemSaleAttrVo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zy.common.utils.PageUtils;
import com.zy.common.utils.Query;

import com.zy.webmail.product.dao.SkuSaleAttrValueDao;
import com.zy.webmail.product.entity.SkuSaleAttrValueEntity;
import com.zy.webmail.product.service.SkuSaleAttrValueService;


@Service("skuSaleAttrValueService")
public class SkuSaleAttrValueServiceImpl extends ServiceImpl<SkuSaleAttrValueDao, SkuSaleAttrValueEntity> implements SkuSaleAttrValueService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuSaleAttrValueEntity> page = this.page(
                new Query<SkuSaleAttrValueEntity>().getPage(params),
                new QueryWrapper<SkuSaleAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    /*保存sku 销售属性*/
    @Override
    public void saveSkuAttr(List<SkuSaleAttrValueEntity> skuAttrs) {
        this.saveBatch(skuAttrs);
    }

    /**
     * 获取spu的销售属性组合
     * @param spuId
     * @return
     */
    @Override
    public List<SkuItemSaleAttrVo> getSaleAttrsBySpuId(Long spuId) {
        SkuSaleAttrValueDao dao = this.baseMapper;
        return dao.getSaleAttrsBySpuId(spuId);
    }

    /**
     * 获取整合销售属性
     * @param skuId
     * @return
     */
    @Override
    public List<String> getSaleAttrValueToStringList(Long skuId) {
        SkuSaleAttrValueDao dao = this.baseMapper;
        return dao.getSaleAttrValueToStringList(skuId);
    }

}