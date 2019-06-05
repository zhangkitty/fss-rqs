package com.znv.fssrqs.vo;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.znv.fssrqs.enums.ErrorCodeEnum;
import com.znv.fssrqs.exception.BusinessException;
import com.znv.fssrqs.exception.UndefinedException;
import jdk.nashorn.internal.objects.annotations.Constructor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class Response {

    private String errcode;

    private String msg;

    private Object data;

    public Response(String errCode,String msg){
        this.errcode = errCode;
        this.msg = msg;
    }

    public static Response returnBusinessException(BusinessException e){
        return new Response(e.getErrcode(),e.getMsg(),null);
    }

    public static Response returnUndefindedException(){
        return new Response(ErrorCodeEnum.UNDIFINITION.getCode(),ErrorCodeEnum.UNDIFINITION.getMessage(),null);
    }

    public static Response success(Object data){
        return new Response(ErrorCodeEnum.SUCCESS.getCode(),ErrorCodeEnum.SUCCESS.getMessage(),data);
    }



}
