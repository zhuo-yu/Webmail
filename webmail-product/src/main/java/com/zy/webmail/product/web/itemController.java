package com.zy.webmail.product.web;

import com.zy.webmail.product.service.SkuInfoService;
import com.zy.webmail.product.vo.SkuItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import java.util.concurrent.ExecutionException;

@Controller
public class itemController {

    @Autowired
    SkuInfoService skuInfoService;

     @GetMapping("/{skuId}.html")
     public String skuItem(@PathVariable("skuId") Long skuId, Model model) throws ExecutionException, InterruptedException {
         model.addAttribute("skuId",skuId);
         SkuItemVo skuItemVo = skuInfoService.item(skuId);
         model.addAttribute("item",skuItemVo);
        return "item";
     }
}
