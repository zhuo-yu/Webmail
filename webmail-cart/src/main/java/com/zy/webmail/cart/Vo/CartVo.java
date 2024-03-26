package com.zy.webmail.cart.Vo;

import java.math.BigDecimal;
import java.util.List;

public class CartVo {

    //商品列表
    private List<CartItemVo> items;

    //商品数量
    private Integer countNum;

    //商品种类数量
    private Integer countType;

    //总价
    private BigDecimal totalAmount = new BigDecimal(0);

    //减免价格
    private BigDecimal reduce = new BigDecimal(0);

    public List<CartItemVo> getItems() {
        return items;
    }

    public void setItems(List<CartItemVo> items) {
        this.items = items;
    }

    //重写商品数量
    public Integer getCountNum() {
        int count = 0;
        if (items !=null && items.size()>0){
            for (CartItemVo item : items) {
                count += item.getCount();
            }
        }
        return count;
    }

    //重写商品种类数量
    public Integer getCountType() {
//        int count = 0;
//        if (items !=null && items.size()>0){
//            for (CartItemVo item : items) {
//                count += 1;
//            }
//        }
//        return count;
        return items != null && items.size() >0?items.size():0;
    }

    //重写总金额
    public BigDecimal getTotalAmount() {
        BigDecimal amount = new BigDecimal(0);
        //统计总金额
        if (items !=null && items.size()>0){
            for (CartItemVo item : items) {
                amount = amount.add(item.getTotalPrice());
            }
        }
        //减去减免金额
        return amount.subtract(reduce);
    }


    public BigDecimal getReduce() {
        return reduce;
    }

    public void setReduce(BigDecimal reduce) {
        this.reduce = reduce;
    }
}
