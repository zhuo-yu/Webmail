package com.zy.webmail.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zy.webmail.product.dao.SkuInfoDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zy.common.utils.PageUtils;
import com.zy.common.utils.Query;

import com.zy.webmail.product.dao.SkuImagesDao;
import com.zy.webmail.product.entity.SkuImagesEntity;
import com.zy.webmail.product.service.SkuImagesService;


@Service("skuImagesService")
public class SkuImagesServiceImpl extends ServiceImpl<SkuImagesDao, SkuImagesEntity> implements SkuImagesService {

    @Autowired
    private SkuImagesDao skuImagesDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuImagesEntity> page = this.page(
                new Query<SkuImagesEntity>().getPage(params),
                new QueryWrapper<SkuImagesEntity>()
        );

        return new PageUtils(page);
    }

    /*保存sku images信息*/
    @Override
    public void saveImage(List<SkuImagesEntity> imageslist) {
        this.saveBatch(imageslist);
    }

    @Override
    public List<SkuImagesEntity> getSkuImageBySkuId(Long skuId) {
//        SkuImagesDao baseMapper = this.baseMapper;
        return skuImagesDao.selectList(new LambdaQueryWrapper<SkuImagesEntity>().eq(SkuImagesEntity::getSkuId, skuId));
    }

}