package com.zy.webmail.webmailthirdservice.controller;

import com.zy.common.utils.R;
import com.zy.webmail.webmailthirdservice.utils.SmsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sms")
public class SmsController {

    @Autowired
    private SmsUtils smsUtils;

    /**
     * 发送短信校验
     * @return
     */
    @GetMapping("/smsSend")
    public R smsSend(@RequestParam("phone") String phone,@RequestParam("code") String code){
        smsUtils.sendSms(phone,code,"","");
        return R.ok();
    }
}
