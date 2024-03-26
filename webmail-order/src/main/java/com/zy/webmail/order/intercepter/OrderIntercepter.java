package com.zy.webmail.order.intercepter;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class OrderIntercepter implements HandlerInterceptor {

    private static final ThreadLocal<JSONObject> loginUser = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        JSONObject memberInfo = (JSONObject) request.getSession().getAttribute("memberInfo");
        if (memberInfo != null){
            loginUser.set(memberInfo);
            return true;
        }else {
            request.getSession().setAttribute("msg","请先登录!");
            response.sendRedirect("http://auth.webmail.com/login.html");
            return false;
        }
    }
}
