package com.znv.fssrqs.common;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.nio.charset.StandardCharsets;
import java.util.List;
@Configuration
public class MyMvcConfiguration extends WebMvcConfigurationSupport {
    /**
     * 建议使用该方法进行HttpMessageConverters的修改，此时的converters已经是Spring初始化过的
     * 因为加入了WebMvcConfigure，导致Spring的HttpMessageConverters中的String转换类默认使用ISO-8859-1编码
     * 所以这里对Spring已经初始化过的StringHttpMessageConverter的编码进行修改
     *
     * @param converters
     */
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.forEach(converter -> {
            if (converter instanceof StringHttpMessageConverter) {
                StringHttpMessageConverter stringConverter = (StringHttpMessageConverter) converter;
                stringConverter.setDefaultCharset(StandardCharsets.UTF_8);
            }
        });
    }
}