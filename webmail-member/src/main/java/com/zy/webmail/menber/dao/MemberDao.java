package com.zy.webmail.menber.dao;

import com.zy.webmail.menber.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author zhuoyu
 * @email 787958123@qq.com
 * @date 2020-04-19 16:10:37
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
