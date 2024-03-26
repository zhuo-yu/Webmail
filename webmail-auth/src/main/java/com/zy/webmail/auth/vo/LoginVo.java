package com.zy.webmail.auth.vo;

import lombok.Data;

@Data
public class LoginVo {

    /**
     * 账号
     */
    private String username;

    /**
     * 密码
     */
    private String password;
}
