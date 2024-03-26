package com.zy.webmail.member.dao;

import com.alibaba.fastjson.JSONObject;
import com.zy.webmail.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zy.webmail.member.entity.MemberLevelEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 会员
 * 
 * @author zhuoyu
 * @email 787958123@qq.com
 * @date 2020-04-19 16:10:37
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {

    @Select("select *from ums_member_level where default_status = 1")
    MemberLevelEntity selectDefaultLevel();

    @Select("select *from ums_member_oauth where uuid = #{id}")
    JSONObject selectOauthUser(JSONObject oauthUser);

    /**
     * 更改其更新时间
     * @param oauth
     */
    @Update("update ums_member_oauth set update_time = now() where id = #{id}")
    void updateOauthUser(JSONObject oauth);

    /**
     * 插入社交用户注册信息表
     * @param oauthUser
     */
    @Insert("insert into ums_member_oauth(create_time,member_id,uuid,oauth_type) values (now(),#{memberId},#{id},#{oauthType})")
    void insertOauthUser(JSONObject oauthUser);
}
