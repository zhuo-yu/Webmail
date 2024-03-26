package com.zy.webmail.member.exception;

public class PhoneExistException extends RuntimeException {
    public PhoneExistException() {
        super("手机号码已存在");
    }
}
