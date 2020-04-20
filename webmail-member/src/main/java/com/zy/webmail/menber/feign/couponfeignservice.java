package com.zy.webmail.menber.feign;


import com.zy.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("coupon-provider")
public interface couponfeignservice {

    @RequestMapping("/coupon/coupon/member/list")
    public R membercoupon();
}
