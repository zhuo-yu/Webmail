package com.zy.webmail.cart.Service;

import com.zy.webmail.cart.Vo.CartItemVo;
import com.zy.webmail.cart.Vo.CartVo;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

public interface CartService {
    CartItemVo addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException;

    CartItemVo getCartItem(Long skuId);

    CartVo getCartList();

    /**
     * 修改缓存购物车数据
     * @param skuId
     * @param isChecked
     * @return
     */
    boolean cartChange(String skuId, String isChecked,Integer num);

    void delCart(String skuId);

}
