package com.znv.fssrqs.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

/**
 * Created by dongzelong on  2019/8/6 10:07.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@Component
public class I18nHelper {
    private static final Logger logger = LoggerFactory.getLogger(I18nHelper.class);

    @Bean(name = "znvMessageSource")
    public MessageSource setSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("i18n.message");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setUseCodeAsDefaultMessage(true);
        return messageSource;
    }

    @Autowired
    @Qualifier("znvMessageSource")
    private MessageSource messageSource;

    @Value("${defaultLocale:zh-CN}")
    private String defaultLocale;

    public String getMessage(String key, Locale locale) {
        locale = null;
        if (locale == null) {
            String[] tmp = defaultLocale.split("-");
            locale = new Locale(tmp[0], tmp[1]);
        }
        String message;
        try {
            message = messageSource.getMessage(key, new Object[]{}, locale);
        } catch (NoSuchMessageException e) {
            message = messageSource.getMessage("50001", new Object[]{}, locale);
        }
        return message;
    }

    public String getMessage(String code, Locale locale, Object... args) {
        locale = null;
        if (locale == null) {
            String[] split = defaultLocale.split("-");
            locale = new Locale(split[0], split[1]);
        }
        String message;
        try {
            message = messageSource.getMessage(code, new Object[]{}, locale);
            message = String.format(message, args);
        } catch (Exception e) {
            message = messageSource.getMessage("50001", new Object[]{}, locale);
            logger.error(message, e);
        }
        return message;
    }

    public String getMessage(String key, HttpServletRequest request) {
        Assert.isTrue(request != null, "request is null");
        Locale locale = null;
        if (locale == null) {
            String[] tmp = defaultLocale.split("-");
            locale = new Locale(tmp[0], tmp[1]);
        }
        String message;
        try {
            message = messageSource.getMessage(key, new Object[]{}, locale);
        } catch (NoSuchMessageException e) {
            message = messageSource.getMessage("501", new Object[]{}, locale);
        }
        return message;
    }
}
