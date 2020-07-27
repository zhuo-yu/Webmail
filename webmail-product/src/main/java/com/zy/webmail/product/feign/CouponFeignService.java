package com.zy.webmail.product.feign;

import com.baomidou.mybatisplus.extension.api.R;
import com.zy.common.to.SkuReductionTo;
import com.zy.common.to.SpuBoundTo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("coupon-provider")
public interface CouponFeignService {

    @PostMapping("/coupon/spubounds/save")  //远程调用接口
    R saveSpuBounds(@RequestBody SpuBoundTo spuBoundTo);

    @PostMapping("/coupon/skufullreduction/saveinfo")
    R saveSkuReduction(SkuReductionTo skuReductionTo);
}
