package com.zy.common.exception;


/*
*   自定义系统状态码
*   错误码规定为5位数
*   前两位为业务场景、后三位表示错误码，例如：10001 10表示通用 001表示系统未知异常
*   维护错误码后需要维护错误描述、将他们定义为枚举形式
*   错误码规范：
*   10:通用
*   11:商品
*   12:订单
*   13:购物车
*   14:物流
*
* */
public enum  exceptionenum {
    UNKNOW_EXCEPTION(10000,"系统未知异常"),
    VAILD_EXCEPTION(10001,"参数格式校验失败"),
    PRODUCT_UP_EXCEPTION(11000,"商品上架异常");

    private int code;
    private String msg;

    exceptionenum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
