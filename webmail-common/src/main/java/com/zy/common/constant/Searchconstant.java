package com.zy.common.constant;

public class Searchconstant {
    public enum SearchStatus{
        CANSEARCH(0,"不可检索"),NOTCANSEARCH(1,"可检索");

        private int code;
        private String msg;
        SearchStatus(int code, String msg) {
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
}
