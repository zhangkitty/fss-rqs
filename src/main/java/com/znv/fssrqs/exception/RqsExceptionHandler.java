package com.znv.fssrqs.exception;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.PascalNameFilter;
import com.fasterxml.jackson.core.JsonParseException;
import com.znv.fssrqs.enums.ErrorCodeEnum;
import com.znv.fssrqs.util.I18nUtils;
import com.znv.fssrqs.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

import static com.znv.fssrqs.util.FastJsonUtils.JsonBuilder.badRequest;
import static com.znv.fssrqs.util.FastJsonUtils.JsonBuilder.error;

@RestControllerAdvice
@Slf4j
public class RqsExceptionHandler {
    @ExceptionHandler({Exception.class})
    public String resolveException(Exception e, HttpServletRequest request) {
//        StackTraceElement[] st = e.getStackTrace();
//        String exclass = new String();
//        String method = new String();
//        for (StackTraceElement stackTraceElement : st) {
//            exclass = stackTraceElement.getClassName();
//            if (exclass.contains(CommonConstant.PACKAGE_PATH_NAME)) {
//                method = stackTraceElement.getMethodName();
//                break;
//            }
//        }
        log.error(e.getMessage(), e);
        Locale locale = request.getLocale();
        if(e instanceof MethodArgumentNotValidException){
            return JSONObject.toJSONString(ResponseVo.getInstance(ErrorCodeEnum.PARAM_ILLEGAL.getCode(),((MethodArgumentNotValidException) e).getBindingResult().getFieldError().getDefaultMessage(),null));
        }
        if (e instanceof BusinessException) {
            return JSONObject.toJSONString(ResponseVo.returnBusinessException((BusinessException) e), new PascalNameFilter());
        } else if (e instanceof BindingResult) {
            return JSONObject.toJSONString(ResponseVo.getInstance(ErrorCodeEnum.PARAM_ILLEGAL.getCode(), ((WebExchangeBindException) e).getFieldError().getDefaultMessage(), null), new PascalNameFilter());
        }
        else if (e instanceof HttpMessageNotReadableException){
            return JSONObject.toJSONString(ResponseVo.getInstance(ErrorCodeEnum.PARAM_ILLEGAL.getCode(), ErrorCodeEnum.PARAM_ILLEGAL.getMessage(),null));
        }
        else if (e instanceof ZnvException) {
            return ((ZnvException) e).json(locale).toString();
        } else if (e instanceof IllegalArgumentException) {
            String message;
            String error = e.getMessage();
            if (error.contains("%")) {
                String[] split = error.split("%");
                message = I18nUtils.i18n(locale, split[0]);
            } else {
                message = I18nUtils.i18n(locale, error);
            }
            return badRequest(message).json().toString();
        } else if (e instanceof NullPointerException) {
            return error("空指针异常").json().toString();
        } else {
            return JSONObject.toJSONString(ResponseVo.returnUndefindedException());
        }
    }
}
