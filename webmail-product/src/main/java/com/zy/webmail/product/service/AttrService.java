package com.zy.webmail.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zy.common.utils.PageUtils;
import com.zy.webmail.product.entity.AttrEntity;
import com.zy.webmail.product.entity.ProductAttrValueEntity;
import com.zy.webmail.product.vo.AttrGroupRelationVo;
import com.zy.webmail.product.vo.AttrRespVo;
import com.zy.webmail.product.vo.AttrVo;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author zhuoyu
 * @email 787958123@qq.com
 * @date 2020-04-18 11:53:40
 */
public interface AttrService extends IService<AttrEntity> {


    PageUtils queryPage(Map<String, Object> params);

    void saveAttr(AttrVo attr);

    PageUtils querybaseAttrPage(Map<String, Object> params, Long catelogId, String attrtype);

    AttrRespVo getDetail(Long attrId);

    void updateAttr(AttrRespVo attr);

    List<AttrEntity> getRelationAttr(Long attrgroupId);

    void deleteRelation(AttrGroupRelationVo[] vos);

    PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId);

    List<ProductAttrValueEntity> listforspu(Long spuId);

    List<Long> selectSearchAttrIds(List<Long> attrIds);
}

