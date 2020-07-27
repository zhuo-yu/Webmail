package com.zy.webmail.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Catagory2Vo {
    private String catalog1Id;  //一级父id
    private List<Catagory3Vo> catalog3List; //三级子分类
    private String id;
    private String name;

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class Catagory3Vo{
        private String catalog2Id;
        private String id;
        private String name;
    }
}
