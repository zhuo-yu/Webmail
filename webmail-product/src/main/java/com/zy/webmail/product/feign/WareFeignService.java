package com.zy.webmail.product.feign;

import com.zy.common.to.SkuHasStockVo;
import com.zy.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("ware-provider")
public interface WareFeignService {

    @PostMapping("/ware/waresku/hasstock")
    public List<SkuHasStockVo> getSkusHasStock(@RequestBody List<Long> skuIds);
}
