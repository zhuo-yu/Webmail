package com.zy.webmail.order.Config;

import com.zy.webmail.order.intercepter.OrderIntercepter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
public class IntercepterConfig implements WebMvcConfigurer {

    @Autowired
    OrderIntercepter orderIntercepter;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(orderIntercepter).addPathPatterns("/**");
    }
}
