package com.zy.webmail.member.controller;

import java.util.Arrays;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.zy.webmail.member.feign.couponfeignservice;
import com.zy.webmail.member.vo.MemberLoginVo;
import com.zy.webmail.member.vo.MemberRegisterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.zy.webmail.member.entity.MemberEntity;
import com.zy.webmail.member.service.MemberService;
import com.zy.common.utils.PageUtils;
import com.zy.common.utils.R;



/**
 * 会员
 *
 * @author zhuoyu
 * @email 787958123@qq.com
 * @date 2020-04-19 16:10:37
 */
@RestController
@RequestMapping("/member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;

    @Autowired
    couponfeignservice couponfeignservice;
    @RequestMapping("/coupon")
    public R test(){
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setNickname("张三");

        R membercoupon = couponfeignservice.membercoupon();
        return R.ok().put("member",memberEntity).put("coupons",membercoupon.get("coupon"));
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("menber:member:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 注册
     */
    @PostMapping("/register")
    public R register(@RequestBody MemberRegisterVo memberRegisterVo){
        return memberService.register(memberRegisterVo);
    }

    /**
     * 登录
     * @return
     */
    @PostMapping("/login")
    public MemberEntity login(@RequestBody MemberLoginVo memberLoginVo){
        return memberService.login(memberLoginVo);
    }

    /**
     * 社交登录
     * @return
     */
    @PostMapping("/oauthLogin")
    public MemberEntity oauthLogin(@RequestBody JSONObject oauthUser){
        return memberService.oauthLogin(oauthUser);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("menber:member:info")
    public R info(@PathVariable("id") Long id){
		MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("menber:member:save")
    public R save(@RequestBody MemberEntity member){
		memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("menber:member:update")
    public R update(@RequestBody MemberEntity member){
		memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("menber:member:delete")
    public R delete(@RequestBody Long[] ids){
		memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
