package com.znv.fssrqs.vo;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.znv.fssrqs.enums.ErrorCodeEnum;
import com.znv.fssrqs.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class ResponseVo {

    private String code;

    private String message;

    private Object data;

    public ResponseVo(String code, String message){
        this.code = code;
        this.message = message;
    }

    public static ResponseVo returnBusinessException(BusinessException e){
        return new ResponseVo(e.getErrcode(),e.getMsg(),null);
    }

    public static ResponseVo returnUndefindedException(){
        return new ResponseVo(ErrorCodeEnum.UNDIFINITION.getCode(),ErrorCodeEnum.UNDIFINITION.getMessage(),null);
    }

    public static ResponseVo success(Object data){
        return new ResponseVo(ErrorCodeEnum.SUCCESS.getCode(),ErrorCodeEnum.SUCCESS.getMessage(),data);
    }



}
