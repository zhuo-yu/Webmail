package com.zy.webmail.product.web;

import com.zy.webmail.product.entity.CategoryEntity;
import com.zy.webmail.product.service.CategoryService;
import com.zy.webmail.product.vo.Catagory2Vo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class IndexController {

    @Autowired
    CategoryService categoryService;

    @GetMapping({"/","index.html"})
    public String indexpage(Model model){
        List<CategoryEntity> cateOne=categoryService.getLevelOne(); //获取一级菜单数据
        model.addAttribute("Catagory",cateOne);
        return "index";
    }

    @GetMapping("/index/catalog.json")
    @ResponseBody
    public Map<String,List<Catagory2Vo>> getCatagoryJSON(){
        Map<String, List<Catagory2Vo>> map=categoryService.getCatagoryJSON();
        return map;
    }
}
