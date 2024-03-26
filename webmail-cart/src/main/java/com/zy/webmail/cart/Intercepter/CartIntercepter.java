package com.zy.webmail.cart.Intercepter;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.spring.util.ObjectUtils;
import com.zy.common.constant.cartconstant;
import com.zy.webmail.cart.Dto.MemberWebDto;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Objects;
import java.util.UUID;

@Component
public class CartIntercepter implements HandlerInterceptor {
    public static ThreadLocal<MemberWebDto> memberInfoLocal = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        MemberWebDto memberWebDto = new MemberWebDto();
        HttpSession session = request.getSession();
        JSONObject memberInfo = (JSONObject) session.getAttribute("memberInfo");
        Cookie[] cookies = request.getCookies();
        if (memberInfo != null) {
            memberWebDto.setUserId(Long.parseLong(memberInfo.get("id").toString()));
        }
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                String name = cookie.getName();
                //如果带有临时cookie,则说明有对应临时数据
                if (cartconstant.WEB_USER_KEY.equals(name)) {
                    memberWebDto.setUserKey(cookie.getValue());
                    memberWebDto.setTempUser(true);
                }
            }
        }
        //如果没有临时用户,则需一定要分配临时用户
        if (StringUtils.isEmpty(memberWebDto.getUserKey())) {
            String uuid = UUID.randomUUID().toString();
            memberWebDto.setUserKey(uuid);
            memberWebDto.setTempUser(false);
        }

        memberInfoLocal.set(memberWebDto);

        System.out.println(memberWebDto.toString());
        return true;
    }


    /**
     * 请求结束后判断是否添加cookie
     *
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        MemberWebDto memberWebDto = memberInfoLocal.get();
        if (Objects.nonNull(memberWebDto) && !memberWebDto.getTempUser()) {
            Cookie cookie = new Cookie(cartconstant.WEB_USER_KEY, memberWebDto.getUserKey());
            cookie.setDomain("webmail.com");
            cookie.setMaxAge(cartconstant.WEB_USER_TIMEOUT);
            response.addCookie(cookie);
        }
    }
}
