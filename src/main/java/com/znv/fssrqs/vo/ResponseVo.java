package com.znv.fssrqs.vo;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.znv.fssrqs.enums.ErrorCodeEnum;
import com.znv.fssrqs.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE)
public class ResponseVo {

    private String Code;

    private String Message;

    private Object Data;

    public ResponseVo(String code, String message){
        this.Code = code;
        this.Message = message;
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
