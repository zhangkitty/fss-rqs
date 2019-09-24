package com.znv.fssrqs.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
public class MyMvcConfiguration extends WebMvcConfigurationSupport {
    @Bean
    public SessionHandler sessionHandler() {
        return new SessionHandler();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        super.addInterceptors(registry);
        registry.addInterceptor(sessionHandler());
    }

    /* 该方法不建议使用，因为此方法接管了Spring初始化时对HttpMessageConverters的初始化过程，接管以后，Spring不再对此进行初始化
     * 该方法在 extendMessageConverters 之前执行
    @Override
    protected void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new StringHttpMessageConverter());
    }*/

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