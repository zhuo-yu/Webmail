package com.zy.webmail.member.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.client.config.utils.MD5;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zy.common.utils.R;
import com.zy.webmail.member.entity.MemberLevelEntity;
import com.zy.webmail.member.exception.PhoneExistException;
import com.zy.webmail.member.exception.UserNameExistException;
import com.zy.webmail.member.vo.MemberLoginVo;
import com.zy.webmail.member.vo.MemberRegisterVo;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zy.common.utils.PageUtils;
import com.zy.common.utils.Query;

import com.zy.webmail.member.dao.MemberDao;
import com.zy.webmail.member.entity.MemberEntity;
import com.zy.webmail.member.service.MemberService;
import org.springframework.transaction.annotation.Transactional;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public R register(MemberRegisterVo memberRegisterVo) {
        MemberDao dao = this.baseMapper;
        checkUserName(memberRegisterVo.getUsername());
        checkPhone(memberRegisterVo.getPhone());
        //查找默认等级
        MemberLevelEntity memberLevelEntity = dao.selectDefaultLevel();
        MemberEntity memberEntity = new MemberEntity();
        //默认等级
        memberEntity.setLevelId(memberLevelEntity.getId());
        //用户名
        memberEntity.setUsername(memberRegisterVo.getUsername());
        //手机号
        memberEntity.setMobile(memberRegisterVo.getPhone());
        //密码,使用md5加密
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String pwd = bCryptPasswordEncoder.encode(memberRegisterVo.getPassword());
        memberEntity.setPassword(pwd);

        dao.insert(memberEntity);
        return R.ok();
    }

    /**
     * 登录
     *
     * @param memberLoginVo
     * @return
     */
    @Override
    public MemberEntity login(MemberLoginVo memberLoginVo) {
        MemberDao dao = this.baseMapper;
        MemberEntity entity = dao.selectOne(new LambdaQueryWrapper<MemberEntity>()
                .eq(MemberEntity::getUsername, memberLoginVo.getUsername())
                .or()
                .eq(MemberEntity::getMobile, memberLoginVo.getUsername()));
        //说明有账户
        if (entity != null){
            String password = entity.getPassword();
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            boolean result = encoder.matches(memberLoginVo.getPassword(), password);
            if (result){
                return entity;
            }
        }
        return null;
    }

    /**
     * 社交登录
     * @param oauthUser
     * @return
     */
    @Override
//    @Transactional(rollbackFor = Exception.class)
    public MemberEntity oauthLogin(JSONObject oauthUser) {
        MemberDao dao = this.baseMapper;
        JSONObject oauth = dao.selectOauthUser(oauthUser);
        MemberEntity entity = new MemberEntity();
        if (oauth != null){
           //说明已有注册信息,更改对应的更新时间即可
            dao.updateOauthUser(oauth);
            entity = dao.selectOne(new LambdaQueryWrapper<MemberEntity>().eq(MemberEntity::getId, oauth.get("member_id")));
        }else {
            //否则,新注册信息
            //插入用户表
            //查找默认等级
            MemberLevelEntity memberLevelEntity = dao.selectDefaultLevel();
            entity.setNickname(oauthUser.get("name")!=null?oauthUser.get("name").toString():null);
            entity.setLevelId(memberLevelEntity.getId());
            entity.setEmail(oauthUser.get("email")!=null?oauthUser.get("email").toString():null);
            entity.setUserImg(oauthUser.get("avatar_url")!=null?oauthUser.get("avatar_url").toString():null);
            dao.insert(entity);
            //插入社交用户注册表
            oauthUser.put("memberId",entity.getId());
            oauthUser.put("oauthType","gitee");
            dao.insertOauthUser(oauthUser);
        }
        return entity;
    }

    /**
     * 检查用户名是否已存在
     */
    private void checkUserName(String userName) throws UserNameExistException {
        MemberDao dao = this.baseMapper;
        Integer count = dao.selectCount(new LambdaQueryWrapper<MemberEntity>().eq(MemberEntity::getUsername, userName));
        if (count > 0) {
            throw new UserNameExistException();
        }
    }

    /**
     * 检查手机号码是否已存在
     */
    private void checkPhone(String phone) throws PhoneExistException {
        MemberDao dao = this.baseMapper;
        Integer count = dao.selectCount(new LambdaQueryWrapper<MemberEntity>().eq(MemberEntity::getMobile, phone));
        if (count > 0) {
            throw new PhoneExistException();
        }

    }

}