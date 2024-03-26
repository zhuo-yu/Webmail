package com.zy.webmail.webmailthirdservice.utils;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Data
@ConfigurationProperties(prefix = "spring.cloud.alicloud")
@Component
public class SmsUtils {
    private String appCode;

    public void sendSms(String phone,String code,String templateCode,String signName){
        if (StringUtils.isEmpty(templateCode)){
            templateCode = "T0001";
        }
        if (StringUtils.isEmpty(signName)){
            signName = "复数科技";
        }
        String host = "http://notifysms.market.alicloudapi.com";
        String path = "/send";
        String method = "POST";
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "APPCODE " + appCode);
        //根据API的要求，定义相对应的Content-Type
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        Map<String, String> querys = new HashMap<String, String>();
        Map<String, String> bodys = new HashMap<String, String>();
        bodys.put("mobile", phone);
        bodys.put("template_code", templateCode);
        bodys.put("params", "{"+"\"code\""+":\""+code+"\"}");
        bodys.put("sign_name", signName);

        try {
            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
            System.out.println(response.toString());
            //获取response的body
            //System.out.println(EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
