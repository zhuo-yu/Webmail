package com.zy.webmail.coupon.controller;

import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zy.webmail.coupon.entity.CouponEntity;
import com.zy.webmail.coupon.service.CouponService;
import com.zy.common.utils.PageUtils;
import com.zy.common.utils.R;



/**
 * 优惠券信息
 *
 * @author zhuoyu
 * @email 787958123@qq.com
 * @date 2020-04-19 15:50:00
 */
@RefreshScope   //开启动态刷新
@RestController
@RequestMapping("coupon/coupon")
public class CouponController {

    @Autowired
    private CouponService couponService;

    /*
    * 测试nacos配置中心
    * */
    @Value("${user.name}")
    private String name;
    @Value("${user.age}")
    private String age;
    @RequestMapping("/testconfig")
    public R testconfig(){
        return R.ok().put("name",name).put("age",age);
    }
    /*
    * 模拟远程调用功能
    * */
    @RequestMapping("/member/list")
    public R membercoupon(){
        CouponEntity couponEntity = new CouponEntity();
        couponEntity.setCouponName("打折优惠卷");
        return R.ok().put("coupon",Arrays.asList(couponEntity));
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("coupon:coupon:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = couponService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("coupon:coupon:info")
    public R info(@PathVariable("id") Long id){
		CouponEntity coupon = couponService.getById(id);

        return R.ok().put("coupon", coupon);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("coupon:coupon:save")
    public R save(@RequestBody CouponEntity coupon){
		couponService.save(coupon);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("coupon:coupon:update")
    public R update(@RequestBody CouponEntity coupon){
		couponService.updateById(coupon);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("coupon:coupon:delete")
    public R delete(@RequestBody Long[] ids){
		couponService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
