package com.zy.webmail.cart.Config;

import com.zy.webmail.cart.Intercepter.CartIntercepter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private CartIntercepter cartIntercepter;
    /**
     * 添加对应拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(cartIntercepter).addPathPatterns("/**");
    }
}
