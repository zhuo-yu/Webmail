package com.zy.webmail.cart.Feign;

import com.zy.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("product-provider")
public interface ProductFeign {

    /**
     * 根据skuid获取对应商品信息
     * @param skuId
     * @return
     */
    @RequestMapping("product/skuinfo/info/{skuId}")
    public R info(@PathVariable("skuId") Long skuId);

    /**
     * 根据skuid获取对应商品销售整合属性数据
     * @param skuId
     * @return
     */
    @GetMapping("product/skusaleattrvalue/getSaleAttrValueToStringList/{skuId}")
    public List<String> getSaleAttrValueToStringList(@PathVariable("skuId") Long skuId);
}
