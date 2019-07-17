package com.znv.fssrqs.enums;


import lombok.Getter;
import lombok.Setter;

@Getter
public enum ErrorCodeEnum {

    // 1****--成功
    SUCCESS(10000,"OK"),

    //2****自定义业务异常
    PARAM_ILLEGAL(20000,"请求入参不合法"),

    //40000
    UNAUTHED_REPEAT_LOGIN(40000,"登录失败，用户已登录"),
    //40001
    UNAUTHED_NOT_LOGIN(40001,"未登录"),
    //40002
    UNAUTHED_LOGIN_ERROR(40002,"用户名或密码错误"),
    //40003
    UNAUTHED_LOCKED(40003,"用户被锁定"),
    //40004
    UNAUTHED_MAX_FAILED_TIMES(40004,"用户失败次数过多"),





    // 50000
    UNDIFINITION(50000,"系统内部异常");

    private Integer code;

    private String message;

    private ErrorCodeEnum(Integer code, String msg) {
        this.code = code;
        this.message = msg;
    }
}
