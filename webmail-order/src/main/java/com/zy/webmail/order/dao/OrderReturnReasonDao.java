package com.zy.webmail.order.dao;

import com.zy.webmail.order.entity.OrderReturnReasonEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 退货原因
 * 
 * @author zhuoyu
 * @email 787958123@qq.com
 * @date 2020-04-19 16:20:53
 */
@Mapper
public interface OrderReturnReasonDao extends BaseMapper<OrderReturnReasonEntity> {
	
}
