package com.zy.webmail.menber.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zy.common.utils.PageUtils;
import com.zy.webmail.menber.entity.MemberLevelEntity;

import java.util.Map;

/**
 * 会员等级
 *
 * @author zhuoyu
 * @email 787958123@qq.com
 * @date 2020-04-19 16:10:37
 */
public interface MemberLevelService extends IService<MemberLevelEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

