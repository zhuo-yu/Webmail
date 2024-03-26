package com.zy.webmail.cart.Service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zy.common.utils.R;
import com.zy.webmail.cart.Dto.MemberWebDto;
import com.zy.webmail.cart.Feign.ProductFeign;
import com.zy.webmail.cart.Intercepter.CartIntercepter;
import com.zy.webmail.cart.Service.CartService;
import com.zy.webmail.cart.Vo.CartItemVo;
import com.zy.webmail.cart.Vo.CartVo;
import com.zy.webmail.cart.Vo.SkuInfoVo;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    ProductFeign productFeign;

    @Autowired
    ThreadPoolExecutor threadPoolExecutor;

    private final static String CART_PREFIX = "webmail:cart:";

    @Override
    public CartItemVo addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException {
        int count = num;
        //redis缓存hash操作
        BoundHashOperations<String, Object, Object> cartOps = getCart();
        ObjectMapper objectMapper = new ObjectMapper();
        //判断是否已有商品在购物车
        Object skuObj = cartOps.get(skuId.toString());
        //如果已经有,则数量叠加
        if (Objects.nonNull(skuObj)){
//            CartItemVo cartItemVo = objectMapper.convertValue(skuObj, CartItemVo.class);
            CartItemVo cartItemVo = JSON.parseObject(skuObj.toString(), CartItemVo.class);
            count += cartItemVo.getCount();
        }
        CartItemVo cartItemVo = new CartItemVo();
        CompletableFuture<Void> skuInfoTask = CompletableFuture.runAsync(() -> {
            //远程查询商品信息
            R skuInfo = productFeign.info(skuId);
            Object skuInfoObject = skuInfo.get("skuInfo");
            SkuInfoVo skuInfoVo = objectMapper.convertValue(skuInfoObject, SkuInfoVo.class);
            cartItemVo.setCheck(true);
            cartItemVo.setImage(skuInfoVo.getSkuDefaultImg());
            cartItemVo.setPrice(skuInfoVo.getPrice());
            cartItemVo.setSkuId(skuId);
            cartItemVo.setTitle(skuInfoVo.getSkuTitle());
        }, threadPoolExecutor);
        //远程查询商品属性
        CompletableFuture<Void> getSkuSaleValue = CompletableFuture.runAsync(() -> {
            List<String> saleAttrValueToStringList = productFeign.getSaleAttrValueToStringList(skuId);
            cartItemVo.setSkuAttr(saleAttrValueToStringList);
        }, threadPoolExecutor);
        cartItemVo.setCount(count);
        CompletableFuture.allOf(skuInfoTask,getSkuSaleValue).get();
        //操作redis数据
        String cartItemVoToString = JSON.toJSONString(cartItemVo);
        cartOps.put(skuId.toString(),cartItemVoToString);
        //回显当前添加数量
        cartItemVo.setCount(num);
        return cartItemVo;
    }

    /**
     * 根据skuid获取对应商品信息
     * @param skuId
     * @return
     */
    @Override
    public CartItemVo getCartItem(Long skuId) {

        return null;
    }

    /**
     * 获取购物车列表
     * @return
     */
    @Override
    public CartVo getCartList() {
        BoundHashOperations<String, Object, Object> cartOpt = getCart();
        List<CartItemVo> CartItemList = new ArrayList<>();
        CartVo cartVo = new CartVo();
        if (cartOpt!=null){
            String key = cartOpt.getKey();
            List<Object> values = cartOpt.values();
            //非临时用户,将临时购物车与非临时购物车合并
            if (key.contains("true")){
                Set<String> keys = redisTemplate.keys(CART_PREFIX + "*");
                for (String k : keys) {
                    BoundHashOperations<String, Object, Object> re1 = redisTemplate.boundHashOps(k);
                    List<Object> values1 = re1.values();
                    for (Object o : values1) {
                        CartItemVo cartItemVo = JSON.parseObject(o.toString(), CartItemVo.class);
                        CartItemList.add(cartItemVo);
                    }
                }
            //合并相同购物项
                Map<String, CartItemVo> map = new HashMap<>();
                for (CartItemVo cartItemVo : CartItemList) {
                    if (cartItemVo != null && cartItemVo.getSkuId() != null){
                        //集合已有,合并相同购物项,叠加数目
                        if (map.containsKey(cartItemVo.getSkuId().toString())){
                            CartItemVo itemVo = map.get(cartItemVo.getSkuId().toString());
                            itemVo.setCount(itemVo.getCount()+cartItemVo.getCount());
                            map.put(cartItemVo.getSkuId().toString(),itemVo);
                        }else {//集合无,添加至集合
                            map.put(cartItemVo.getSkuId().toString(),cartItemVo);
                        }
                    }
                }
                CartItemList = map.entrySet().stream().map(res -> {
                    return res.getValue();
                }).collect(Collectors.toList());
                //删除临时用户的购物项
                Set<String> tempCartItem = redisTemplate.keys(CART_PREFIX + "false:*");
                tempCartItem.forEach(res->{
                    redisTemplate.delete(res);
                });
            }
            //临时用户
            if (key.contains("false")){
                CartItemList = values.stream().map(res -> {
                    CartItemVo cartItemVo = JSON.parseObject(res.toString(), CartItemVo.class);
                    return cartItemVo;
                }).collect(Collectors.toList());
            }
            cartVo.setItems(CartItemList);
        }
        return cartVo;
    }

    /**
     * 修改购物车缓存数据
     * @param skuId
     * @param isChecked
     * @param num
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cartChange(String skuId, String isChecked, Integer num) {
        try {
            BoundHashOperations<String, Object, Object> cartOps = getCart();
            List<Object> values = cartOps.values();
            Object o = cartOps.get(skuId);
            CartItemVo cartItemVo = JSON.parseObject(o.toString(), CartItemVo.class);
            if (num != null){
                cartItemVo.setCount(num);
            }
            if (StringUtils.isNotBlank(isChecked)){
                cartItemVo.setCheck("1".equals(isChecked));
            }
            //删除覆盖
            cartOps.delete(skuId);
            cartOps.put(skuId, JSON.toJSONString(cartItemVo));
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 删除购物项
     * @param skuId
     */
    @Override
    public void delCart(String skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCart();
        cartOps.delete(skuId);
    }

    public BoundHashOperations<String, Object, Object> getCart() {
        MemberWebDto memberWebDto = CartIntercepter.memberInfoLocal.get();
        String cartKey = "";
        //非临时用户
        if (memberWebDto.getUserId() != null){
            cartKey = CART_PREFIX + "true:" + memberWebDto.getUserId();
        }else {
            cartKey = CART_PREFIX + "false:" + memberWebDto.getUserKey();
        }


        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(cartKey);
        return operations;
}
}
