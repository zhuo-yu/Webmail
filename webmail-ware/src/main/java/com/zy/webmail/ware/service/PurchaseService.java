package com.zy.webmail.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zy.common.utils.PageUtils;
import com.zy.webmail.ware.entity.PurchaseEntity;
import com.zy.webmail.ware.vo.MergeVo;

import java.util.Map;

/**
 * 采购信息
 *
 * @author zhuoyu
 * @email 787958123@qq.com
 * @date 2020-04-19 16:26:40
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryunreceive(Map<String, Object> params);

    void mergePurchase(MergeVo mergeVo);
}

