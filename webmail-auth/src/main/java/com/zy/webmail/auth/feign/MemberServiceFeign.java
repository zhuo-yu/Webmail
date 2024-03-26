package com.zy.webmail.auth.feign;

import com.alibaba.fastjson.JSONObject;
import com.zy.common.utils.R;
import com.zy.webmail.auth.vo.LoginVo;
import com.zy.webmail.auth.vo.RegisterVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("member-provider")
public interface MemberServiceFeign {

    @PostMapping("/member/member/register")
    public R register(RegisterVo registerVo);

    @PostMapping("/member/member/login")
    public LoginVo login(@RequestBody LoginVo memberLoginVo);

    @PostMapping("/member/member/oauthLogin")
    public JSONObject oauthLogin(@RequestBody JSONObject oauthUser);
}
