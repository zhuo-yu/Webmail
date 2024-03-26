package com.zy.webmail.auth.Controller;

import com.alibaba.fastjson.JSONObject;
import com.zy.common.utils.HttpUtils;
import com.zy.webmail.auth.feign.MemberServiceFeign;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
public class OAuthController {

    @Autowired
    MemberServiceFeign memberServiceFeign;

    @GetMapping("/oauthLogin")
    public String oauthLogin(@RequestParam("code") String code, Model model, RedirectAttributes attributes, HttpSession session) throws Exception {
        Map<String, String> queryBody = new HashMap<>();
        queryBody.put("grant_type","authorization_code");
        queryBody.put("code",code);
        queryBody.put("client_id","3cec8ca1126941475ff9c8e68ff54d27727017841a59c34ca0e6965c45be0321");
        queryBody.put("client_secret","6793d469008c512f1a205e1b2d867959167a19048d390689470bd05947e68c9e");
        queryBody.put("redirect_uri","http://auth.webmail.com/oauthLogin");
        HttpResponse httpResponse = HttpUtils.doPost("https://gitee.com", "/oauth/token", "post", new HashMap<>(), new HashMap<>(), queryBody);
        if (httpResponse.getStatusLine().getStatusCode() == 200){
            String entityStr = EntityUtils.toString(httpResponse.getEntity());
            JSONObject jsonObject = JSONObject.parseObject(entityStr);
            Map<String, String> queryParams = new HashMap<>();
            queryParams.put("access_token",jsonObject.get("access_token").toString());
            HttpResponse oauthUserInfo = HttpUtils.doGet("https://gitee.com", "/api/v5/user", "get", new HashMap<>(), queryParams);
            //拿到最终社交服务器用户信息
            if (oauthUserInfo.getStatusLine().getStatusCode() == 200){
                String oauthUserStr = EntityUtils.toString(oauthUserInfo.getEntity());
                JSONObject oauthUser = JSONObject.parseObject(oauthUserStr);
                //注册对应的会员信息
                JSONObject memberObj = memberServiceFeign.oauthLogin(oauthUser);
                model.addAttribute("memberInfo",memberObj);
                session.setAttribute("memberInfo",memberObj);
                return "redirect:http://webmail.com";
            }
            System.out.println(entityStr);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("msg","登录失败!");
        attributes.addFlashAttribute("errors",map);
        return "redirect:http://auth.webmail.com/login.html";
    }
}
