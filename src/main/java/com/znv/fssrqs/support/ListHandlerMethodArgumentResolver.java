package com.znv.fssrqs.support;

import com.znv.fssrqs.annotation.ListParam;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Arrays;

/**
 * Created by dongzelong on  2019/9/2 13:56.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
public class ListHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.hasParameterAnnotation(ListParam.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        final ListParam param = parameter.getParameterAnnotation(ListParam.class);
        if (param == null) {
            throw new IllegalArgumentException("Unknown parameter type [" + parameter.getParameterType().getName() + "]");
        }

        String name = "".equalsIgnoreCase(param.name()) ? param.value() : param.name();
        if ("".equalsIgnoreCase(name)) {
            name = parameter.getParameter().getName();
        }

        String ans = nativeWebRequest.getParameter(name);
        if (ans == null) {
            return null;
        }

        String[] cells = ans.split(",");
        return Arrays.asList(cells);
    }
}