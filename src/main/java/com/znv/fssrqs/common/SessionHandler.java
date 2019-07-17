package com.znv.fssrqs.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.enums.ErrorCodeEnum;
import com.znv.fssrqs.exception.BusinessException;
import com.znv.fssrqs.vo.ResponseVo;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class SessionHandler implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        Object user = request.getSession().getAttribute("user");
        if (user == null) {
            if ("/user/login".equals(request.getRequestURI())) {
                return true;
            } else {
                ResponseVo responseVo = ResponseVo.returnBusinessException(
                        new BusinessException(ErrorCodeEnum.UNAUTHED_NOT_LOGIN));
                response.getWriter().write(JSON.toJSONString(responseVo));
                return false;
            }
        } else {
            if ("/user/login".equals(request.getRequestURI())) {
                ResponseVo responseVo = ResponseVo.returnBusinessException(
                        new BusinessException(ErrorCodeEnum.UNAUTHED_REPEAT_LOGIN));
                response.getWriter().write(JSON.toJSONString(responseVo));
                return false;
            }
            return true;
        }
    }
}
