package com.znv.fssrqs.enums;


import lombok.Getter;
import lombok.Setter;

@Getter
public enum ErrorCodeEnum {

    // 1****--成功
    SUCCESS(10000,"OK"),

    //2****自定义业务异常
    PARAM_ILLEGAL(20000,"请求入参不合法"),






    // 50000
    UNDIFINITION(50000,"系统内部异常");

    private Integer code;

    private String message;

    private ErrorCodeEnum(Integer code, String msg) {
        this.code = code;
        this.message = msg;
    }
}
