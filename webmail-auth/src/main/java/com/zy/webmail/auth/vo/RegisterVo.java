package com.zy.webmail.auth.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
public class RegisterVo {

    @NotEmpty(message = "用户名不可为空")
    private String username;

    @NotEmpty(message = "密码不可为空")
    @Length(min = 3,max = 8,message = "密码长度需在3-8位")
    private String password;

    @NotEmpty(message = "手机号不可为空")
//    @Pattern(regexp = "/^1[3456789]\\d{9}$/",message = "手机格式不正确")
    private String phone;

    @NotEmpty(message = "验证码不可为空")
    private String code;
}
