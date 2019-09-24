package com.znv.fssrqs.exception;


import com.znv.fssrqs.enums.ErrorCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
//@NoArgsConstructor
public class UndefinedException extends Exception {

    private Integer errcode;
    private String msg;

    public UndefinedException(ErrorCodeEnum errorCodeEnum) {
        this.errcode = errorCodeEnum.getCode();
        this.msg = errorCodeEnum.getMessage();
    }
}
