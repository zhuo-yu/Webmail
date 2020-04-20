package com.zy.webmail.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zy.common.utils.PageUtils;
import com.zy.webmail.product.entity.BrandEntity;

import java.util.Map;

/**
 * 品牌
 *
 * @author zhuoyu
 * @email 787958123@qq.com
 * @date 2020-04-18 11:53:40
 */
public interface BrandService extends IService<BrandEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

