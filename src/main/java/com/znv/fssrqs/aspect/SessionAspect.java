package com.znv.fssrqs.aspect;

import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.constant.CommonConstant;
import com.znv.fssrqs.enums.ErrorCodeEnum;
import com.znv.fssrqs.exception.BusinessException;
import com.znv.fssrqs.exception.ZnvException;
import com.znv.fssrqs.util.I18nUtils;
import com.znv.fssrqs.util.LocalUserUtil;
import com.znv.fssrqs.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Locale;

/**
 * Created by dongzelong on  2019/1/25 19:45.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@Aspect
@Component
@Slf4j
@Configuration
public class SessionAspect {
    @Value("${conf.defaultUserId:}")
    private String defaultUserId;

    @Pointcut("within(com.znv.fssrqs.controller..*) " +
            "&& !within(com.znv.fssrqs.controller.LoginController) " +
            "&& !within(com.znv.fssrqs.controller.SystemController)" +
            "&& !within(com.znv.fssrqs.controller.HomePageController)" +
            "&& !within(com.znv.fssrqs.controller.reid..*)")
    public void checkSession(){}

    @Around("checkSession()")
    public Object beforeCheckSession(ProceedingJoinPoint point) throws Throwable{
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        if (request == null) {
            log.info("Aspect [{}]", Thread.currentThread().getId());
            return point.proceed();
        }

        log.info("Aspect [{}] Url: {}", Thread.currentThread().getId(), request.getRequestURI());
        if (!StringUtils.isEmpty(defaultUserId)) {
            JSONObject user = new JSONObject();
            user.put("UserId", defaultUserId);
            LocalUserUtil.setLocalUser(user);
            return point.proceed();
        }

        HttpSession session= request.getSession();

        JSONObject userLogin = (JSONObject)session.getAttribute("UserLogin");
        if (userLogin == null || !userLogin.containsKey("UserId")) {
            /*Locale locale = request.getLocale();
            Class returnClass = ((MethodSignature)point.getSignature()).getReturnType();
            if (returnClass.equals(ResponseVo.class) ) {
                ResponseVo retObject = ResponseVo.getInstance(
                        ErrorCodeEnum.UNAUTHED_NOT_LOGIN.getCode(), I18nUtils.i18n(locale, "UserNotLogin"), null);
                return retObject;
            } else if (returnClass.equals(JSONObject.class) || returnClass.equals(String.class)) {
                JSONObject retObject = new JSONObject();
                retObject.put("Message", I18nUtils.i18n(locale, "UserNotLogin"));
                retObject.put("Code", ErrorCodeEnum.UNAUTHED_NOT_LOGIN.getCode());
                if (returnClass.equals(JSONObject.class)) {
                    return retObject;
                } else {
                    return retObject.toJSONString();
                }
            } else {
                throw new BusinessException(ErrorCodeEnum.UNAUTHED_NOT_LOGIN);
            }*/

            throw ZnvException.error(ErrorCodeEnum.UNAUTHED_NOT_LOGIN.getCode(), "UserNotLogin");
        } else {
            LocalUserUtil.setLocalUser(userLogin);
            return point.proceed();
        }
    }

}
