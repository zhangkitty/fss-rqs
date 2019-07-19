package com.znv.fssrqs.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.enums.ErrorCodeEnum;
import com.znv.fssrqs.exception.BusinessException;
import com.znv.fssrqs.util.LocalUserUtil;
import com.znv.fssrqs.vo.ResponseVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SessionHandler implements HandlerInterceptor {
    @Value("${conf.defaultUserId:}")
    private String defaultUserId;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        if (!StringUtils.isEmpty(defaultUserId)) {
            LocalUserUtil.setLocalUserId(defaultUserId);
            return true;
        }

        if ("/user/login".equals(request.getRequestURI())) {
            return true;
        }

        JSONObject userLogin = (JSONObject)request.getSession().getAttribute("UserLogin");
        if (userLogin == null) {
            ResponseVo responseVo = ResponseVo.returnBusinessException(
                    new BusinessException(ErrorCodeEnum.UNAUTHED_NOT_LOGIN));
            response.getWriter().write(JSON.toJSONString(responseVo));
            return false;
        } else {
            LocalUserUtil.setLocalUserId(userLogin.getString("UserId"));
            return true;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex)
            throws Exception {
        LocalUserUtil.removeLocalUserId();
    }
}
