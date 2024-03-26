package com.zy.webmail.member.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zy.common.utils.PageUtils;
import com.zy.common.utils.R;
import com.zy.webmail.member.entity.MemberEntity;
import com.zy.webmail.member.vo.MemberLoginVo;
import com.zy.webmail.member.vo.MemberRegisterVo;

import java.util.Map;

/**
 * 会员
 *
 * @author zhuoyu
 * @email 787958123@qq.com
 * @date 2020-04-19 16:10:37
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    R register(MemberRegisterVo memberRegisterVo);

    /**
     * 登录
     * @param memberLoginVo
     * @return
     */
    MemberEntity login(MemberLoginVo memberLoginVo);

    /**
     * 社交登录
     * @param oauthUser
     * @return
     */
    MemberEntity oauthLogin(JSONObject oauthUser);

}

