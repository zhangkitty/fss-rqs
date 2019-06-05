package com.znv.fssrqs.enums;


import lombok.Getter;
import lombok.Setter;

@Getter
public enum ErrorCodeEnum {

    // 1***--成功
    SUCCESS("10000","OK"),

    //2***自定义业务异常
    PARAM_ILLEGAL("20000","请求入参不合法"),






    // 500
    UNDIFINITION("500","系统内部异常");

    private String code;

    private String message;

    private ErrorCodeEnum(String code, String msg) {
        this.code = code;
        this.message = msg;
    }
}
