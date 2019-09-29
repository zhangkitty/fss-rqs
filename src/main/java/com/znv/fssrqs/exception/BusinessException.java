package com.znv.fssrqs.exception;


import com.znv.fssrqs.enums.ErrorCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
//@NoArgsConstructor
public class BusinessException extends RuntimeException {

    private Integer errcode;
    private String msg;

    public BusinessException(ErrorCodeEnum errorCodeEnum) {
        this.errcode = errorCodeEnum.getCode();
        this.msg = errorCodeEnum.getMessage();
    }
}
