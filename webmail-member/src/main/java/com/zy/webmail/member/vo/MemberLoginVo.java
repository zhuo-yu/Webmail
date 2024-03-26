package com.zy.webmail.member.vo;

import lombok.Data;

@Data
public class MemberLoginVo {

    /**
     * 账号
     */
    private String username;

    /**
     * 密码
     */
    private String password;
}
