package com.zy.common.to;

import lombok.Data;

@Data
public class SkuHasStockVo {
    private Long skuId;
    private boolean hasStock;

    public boolean isHasStock() {
        return hasStock;
    }

    public void setHasStock(boolean hasStock) {
        this.hasStock = hasStock;
    }
}
