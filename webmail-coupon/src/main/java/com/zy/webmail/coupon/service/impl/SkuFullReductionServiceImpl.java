package com.zy.webmail.coupon.service.impl;

import com.zy.common.to.MemberPrice;
import com.zy.common.to.SkuReductionTo;
import com.zy.webmail.coupon.entity.MemberPriceEntity;
import com.zy.webmail.coupon.entity.SkuLadderEntity;
import com.zy.webmail.coupon.service.MemberPriceService;
import com.zy.webmail.coupon.service.SkuLadderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zy.common.utils.PageUtils;
import com.zy.common.utils.Query;

import com.zy.webmail.coupon.dao.SkuFullReductionDao;
import com.zy.webmail.coupon.entity.SkuFullReductionEntity;
import com.zy.webmail.coupon.service.SkuFullReductionService;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {

    @Autowired
    SkuLadderService skuLadderService;

    @Autowired
    MemberPriceService memberPriceService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuReduction(SkuReductionTo skuReductionTo) {
       // sku的优惠、满减信息  webmail-sms->`sms_sku_full_reduction` and`sms_sku_ladder` and `sms_member_price`
        //1、 and`sms_sku_ladder
        SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
        skuLadderEntity.setSkuId(skuReductionTo.getSkuId());
        skuLadderEntity.setFullCount(skuReductionTo.getFullCount()); //满几件
        skuLadderEntity.setDiscount(skuReductionTo.getDiscount());   //打几折
        skuLadderEntity.setAddOther(skuReductionTo.getCountStatus()); //打折的状态是否参与其他优惠
//        skuLadderEntity.setPrice();  //打折后的价格
        if(skuLadderEntity.getFullCount()>0){    //如果有打折价
            skuLadderService.save(skuLadderEntity);   //则保存打折价钱
        }


        //2、sms_sku_full_reduction
        SkuFullReductionEntity fullReductionEntity = new SkuFullReductionEntity();
        BeanUtils.copyProperties(skuReductionTo,fullReductionEntity);
        if(fullReductionEntity.getFullPrice().compareTo(new BigDecimal("0"))==1){  //如果有满减价
            this.save(fullReductionEntity);
        }


        //3、sms_member_price 会员价格
        List<MemberPrice> memberPrice = skuReductionTo.getMemberPrice();
        List<MemberPriceEntity> collect = memberPrice.stream().map(item -> {
            MemberPriceEntity memberPriceEntity = new MemberPriceEntity();
            memberPriceEntity.setMemberLevelId(item.getId());  //会员等级id
            memberPriceEntity.setSkuId(skuReductionTo.getSkuId());
            memberPriceEntity.setMemberLevelName(item.getName());  //会员等级名
            memberPriceEntity.setMemberPrice(item.getPrice()); //会员对应价格
            memberPriceEntity.setAddOther(1);
            return memberPriceEntity;
        }).filter(item->{
            return item.getMemberPrice().compareTo(new BigDecimal("0"))==1;
        }).collect(Collectors.toList());
        memberPriceService.saveBatch(collect);
    }

}