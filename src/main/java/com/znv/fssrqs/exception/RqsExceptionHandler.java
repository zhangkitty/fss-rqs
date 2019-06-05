package com.znv.fssrqs.exception;

import com.znv.fssrqs.common.Consts;
import com.znv.fssrqs.enums.ErrorCodeEnum;
import com.znv.fssrqs.vo.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class RqsExceptionHandler {

    @ExceptionHandler({ Exception.class })
    public Response resolveException(Exception e){

        StackTraceElement[] st = e.getStackTrace();
        String exclass = new String() ;
        String method  = new String() ;
        for (StackTraceElement stackTraceElement : st) {
            exclass = stackTraceElement.getClassName();
            if(exclass.contains(Consts.PACKAGE_PATH_NAME)){
                method = stackTraceElement.getMethodName();
                break ;
            }
        }

        if (e instanceof BusinessException) {
            return Response.returnBusinessException((BusinessException)e);
        } else{
            //todo
            // 可以更好的完善一下
            if(e instanceof BindingResult){
                return new Response(ErrorCodeEnum.PARAM_ILLEGAL.getCode(), ((WebExchangeBindException) e).getFieldError().getDefaultMessage());
            }else {
                log.error("非自定义异常:时间-{} 类名-{} 方法-{}",LocalDateTime.now().toString(),exclass,method);
                return Response.returnUndefindedException();
            }

        }

    }

}
