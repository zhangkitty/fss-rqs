package com.znv.fssrqs.vo;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.znv.fssrqs.enums.ErrorCodeEnum;
import com.znv.fssrqs.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class ResponseVo {

    private String code;

    private String message;

    private Object data;

    private static ResponseVo responseVo;

    private ResponseVo(String code, String message,Object data){
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static ResponseVo getInstance(String code, String message,Object data){
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



}
