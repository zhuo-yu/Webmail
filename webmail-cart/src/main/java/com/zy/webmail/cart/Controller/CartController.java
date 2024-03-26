package com.zy.webmail.cart.Controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zy.common.constant.cartconstant;
import com.zy.webmail.cart.Dto.MemberWebDto;
import com.zy.webmail.cart.Intercepter.CartIntercepter;
import com.zy.webmail.cart.Service.CartService;
import com.zy.webmail.cart.Vo.CartItemVo;
import com.zy.webmail.cart.Vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Controller
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @GetMapping("/cartList.html")
    public String cartPage(Model model){
        CartVo cartVo = cartService.getCartList();
        BigDecimal totalAmount = cartVo.getTotalAmount();
        model.addAttribute("cart",cartVo);
        return "cartList";
    }

    @PostMapping("/cartChange")
    @ResponseBody
    public Map cartChange(@RequestParam(value = "isChecked",required = false) String isChecked,@RequestParam("skuId") String skuId,@RequestParam(value = "num",required = false) Integer num){
        Map<String, Object> map = new HashMap<>();
        boolean status = cartService.cartChange(skuId,isChecked,num);
        map.put("status",200);
        if (!status){
            map.put("status",500);
            map.put("msg","数据改变异常!");
        }
        return map;
    }

    @GetMapping("/delCart/{skuId}")
    public String delCart(@PathVariable("skuId") String skuId){
        cartService.delCart(skuId);
        return "redirect:http://cart.webmail.com/cartList.html";
    }

    @GetMapping("/addToCart")
    public String successPage(@RequestParam("skuId") Long skuId,
                              @RequestParam("num") Integer num) throws ExecutionException, InterruptedException {
        ReentrantLock lock = new ReentrantLock();
        CartItemVo cartItemVo = cartService.addToCart(skuId, num);
        String cartItemVoToString = JSON.toJSONString(cartItemVo);
        lock.lock();
        //删除暂存对象键
        redisTemplate.delete(skuId.toString());
        //暂存对象
        redisTemplate.opsForValue().set("temp:"+skuId.toString(),cartItemVoToString,60, TimeUnit.MINUTES);
        lock.unlock();
        return "redirect:http://cart.webmail.com/addToCartSuccessPage/"+skuId;
    }

    /**
     * 跳转至成功页面,防止重复提交
     * @return
     */
    @GetMapping("/addToCartSuccessPage/{skuId}")
    public String addToCartSuccessPage(@PathVariable("skuId") Long skuId, Model model){
//        CartItemVo cartItemVo = cartService.getCartItem(skuId);
        String cartItemStr = redisTemplate.opsForValue().get("temp:"+skuId.toString());
        CartItemVo cartItemVo = JSONObject.parseObject(cartItemStr, CartItemVo.class);
        model.addAttribute("item",cartItemVo);
        return "success";
    }
}
