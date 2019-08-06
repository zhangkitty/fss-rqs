package com.znv.fssrqs.exception;

import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.util.I18nUtils;

import java.util.Locale;

import static com.znv.fssrqs.util.FastJsonUtils.JsonBuilder;

public class ZnvException extends RuntimeException {

    private final JsonBuilder builder;
    private final String errCode;
    private final Object[] args;

    private ZnvException(JsonBuilder builder, String errCode, Object... args) {
        super(errCode);
        this.builder = builder;
        this.errCode = errCode;
        this.args = args;
    }

    private ZnvException(Throwable cause, JsonBuilder builder, String errCode, Object... args) {
        super(errCode, cause);
        this.builder = builder;
        this.errCode = errCode;
        this.args = args;
    }

    public static ZnvException badRequest(String errCode, Object... args) {
        return new ZnvException(JsonBuilder.badRequest(), errCode, args);
    }

    public static ZnvException badRequest(int code, String errCode, Object... args) {
        return new ZnvException(JsonBuilder.badRequest(code), errCode, args);
    }

    public static ZnvException error(String errCode, Object... args) {
        return new ZnvException(JsonBuilder.error(), errCode, args);
    }

    public static ZnvException error(int code, String errCode, Object... args) {
        return new ZnvException(JsonBuilder.error(code), errCode, args);
    }

    public static ZnvException requestFailed(int code, String errCode, Object... agrs) {
        return new ZnvException(JsonBuilder.failed(code), errCode, agrs);
    }

    public static ZnvException notFound(Throwable cause, String resource, String identity) {
        return new ZnvException(cause, JsonBuilder.badRequest(), "NotFound", resource, identity);
    }

    public static Exception moreThanOne(String resource, String identity) {
        return new ZnvException(JsonBuilder.error(), "MoreThanOne", resource, identity);
    }

    public static ZnvException notFound(String resource, String identity) {
        return new ZnvException(JsonBuilder.badRequest(), "NotFound", resource, identity);
    }

    public static ZnvException badFormat(String value) {
        return new ZnvException(JsonBuilder.badRequest(), "BadFormat", value);
    }

    public static ZnvException duplicatedWith(Throwable cause, String resource, String identity) {
        return new ZnvException(cause, JsonBuilder.badRequest(), "DuplicatedWith", resource, identity);
    }

    public static ZnvException dataIntegrityViolation(Throwable cause) {
        return new ZnvException(cause, JsonBuilder.badRequest(), "DataIntegrityViolation");
    }

    public static ZnvException dataAccessFailed(Throwable cause) {
        return new ZnvException(cause, JsonBuilder.error(), "DataAccessFailed");
    }

    public JSONObject json(Locale locale) {
        return builder.message(I18nUtils.i18n(locale, errCode, args)).json();
    }
}
