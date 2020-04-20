package com.zy.webmail.coupon.dao;

import com.zy.webmail.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author zhuoyu
 * @email 787958123@qq.com
 * @date 2020-04-19 15:50:00
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
