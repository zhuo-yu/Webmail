package com.zy.webmail.auth.Controller;

import com.zy.common.constant.authconstant;
import com.zy.common.utils.R;
import com.zy.webmail.auth.feign.MemberServiceFeign;
import com.zy.webmail.auth.feign.ThirdServiceFeign;
import com.zy.webmail.auth.vo.LoginVo;
import com.zy.webmail.auth.vo.RegisterVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Controller
public class LoginController {
    @Autowired
    private ThirdServiceFeign thirdServiceFeign;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    private MemberServiceFeign memberServiceFeign;

//    @GetMapping("/login.html")
//    public String loginPage(){
//        return "login";
//    }
//
//    @GetMapping("/register.html")
//    public String registerPage(){
//        return "register";
//    }

    /**
     * 发送短信校验
     *
     * @return
     */
    @GetMapping("/sendCode")
    @ResponseBody
    public R sendCode(String phone) throws ParseException {
        String resultCode = redisTemplate.opsForValue().get(authconstant.smsPrefix + phone);
        //验证码校验,防止重刷
        if (StringUtils.isNotBlank(resultCode)) {
            //该验证码剩余时间
            Long expireTime = redisTemplate.getExpire(authconstant.smsPrefix + phone);
            //在60秒之内再次点击,则限制请求接口
            if (expireTime > 240) {
                return R.error(10002, "60秒内请勿重发验证码");
            }
        }
        String code = UUID.randomUUID().toString().substring(0, 6);
        redisTemplate.opsForValue().set(authconstant.smsPrefix + phone, code, 300, TimeUnit.SECONDS);
        thirdServiceFeign.smsSend(phone, code);
        return R.ok();
    }

    @PostMapping("/register")
    public String register(@Valid RegisterVo registerVo, BindingResult result, RedirectAttributes attributes){
        if (result.hasErrors()){
            //校验登录信息
            Map<String, String> resultError = result.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            attributes.addFlashAttribute("errors",resultError);
            return "redirect:http://auth.webmail.com/register.html";
        }
        //校验验证码
        String nowCode = redisTemplate.opsForValue().get(authconstant.smsPrefix + registerVo.getPhone());
        if (StringUtils.isNotBlank(nowCode)){
            if (registerVo.getCode().equals(nowCode)){
                //注册信息
                R r = memberServiceFeign.register(registerVo);
                if (r.get("code").equals(0)){
                    //删除已有验证码
                    redisTemplate.delete(authconstant.smsPrefix + registerVo.getPhone());
                    return "redirect:http://auth.webmail.com/login.html";
                }else {
                    //校验错误
                    Map<String, String> errorMap = new HashMap<>();
                    errorMap.put("msg","注册失败!");
                    attributes.addFlashAttribute("errors",errorMap);
                    return "redirect:http://auth.webmail.com/register.html";
                }
            }else {
                //校验错误
                Map<String, String> errorMap = new HashMap<>();
                errorMap.put("code","验证码错误!");
                attributes.addFlashAttribute("errors",errorMap);
                return "redirect:http://auth.webmail.com/register.html";
            }
        }else {
            //校验错误
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("code","验证码错误!");
            attributes.addFlashAttribute("errors",errorMap);
            return "redirect:http://auth.webmail.com/register.html";
        }
    }

    @PostMapping("/login")
    public String login(LoginVo loginVo,RedirectAttributes attributes){
        LoginVo login = memberServiceFeign.login(loginVo);
        if (login != null){
            return "redirect:http://webmail.com";
        }else {
            Map<String, Object> map = new HashMap<>();
            map.put("msg","账户密码错误");
            attributes.addFlashAttribute("errors",map);
            return "redirect:http://auth.webmail.com/login.html";
        }
    }

}
