package com.znv.fssrqs.vo;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.znv.fssrqs.enums.ErrorCodeEnum;
import com.znv.fssrqs.exception.BusinessException;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE)
public class ResponseVo {

    private Integer Code;

    private String Message;

    private Object Data;

    private static ResponseVo responseVo;

    public Integer getCode() {
        return Code;
    }

    public ResponseVo setCode(Integer code) {
        this.Code = code;
        return this;
    }

    public String getMessage() {
        return Message;
    }

    public ResponseVo setMessage(String message) {
        this.Message = message;
        return this;
    }

    public Object getData() {
        return Data;
    }

    public ResponseVo setData(Object data) {
        this.Data = data;
        return this;
    }

    private ResponseVo(Integer code, String message, Object data){
        this.Code = code;
        this.Message = message;
        this.Data = data;
    }

    public static ResponseVo getInstance(Integer code, String message,Object data){
        if(ResponseVo.responseVo==null){
            responseVo = new ResponseVo(code,message,data);
        }else{
            responseVo.setCode(code);
            responseVo.setMessage(message);
            responseVo.setData(data);
        }
        return  responseVo;
    }

    public static ResponseVo returnBusinessException(BusinessException e){
        return ResponseVo.getInstance(e.getErrcode(),e.getMsg(),null);
    }

    public static ResponseVo returnUndefindedException(){
        return ResponseVo.getInstance(ErrorCodeEnum.UNDIFINITION.getCode(),ErrorCodeEnum.UNDIFINITION.getMessage(),null);
    }

    public static ResponseVo success(Object data){
        return ResponseVo.getInstance(ErrorCodeEnum.SUCCESS.getCode(),ErrorCodeEnum.SUCCESS.getMessage(),data);
    }

    public static ResponseVo success(String message,Object data){
        return ResponseVo.getInstance(ErrorCodeEnum.SUCCESS.getCode(),message,data);
    }

    public static ResponseVo error(String message){
        return ResponseVo.getInstance(ErrorCodeEnum.ERROR_GENERAL.getCode(),message,null);
    }



}
