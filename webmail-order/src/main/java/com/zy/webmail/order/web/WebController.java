package com.zy.webmail.order.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class WebController {

    @GetMapping("/{path}.html")
    public String payRoute(@PathVariable String path){
        return path;
    }

    @GetMapping("/toTrade")
    public String toTrade(){
        return "confirm";
    }
}
