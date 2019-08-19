package com.znv.fssrqs.util;

import com.znv.fssrqs.constant.CommonConstant;
import com.znv.fssrqs.exception.ZnvException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by dongzelong on  2019/8/17 22:58.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@Slf4j
public class OkHttpUtil {
    private static OkHttpClient singleton;

    private OkHttpUtil() {

    }

    public static OkHttpClient getInstance() {
        if (singleton == null) {
            synchronized (OkHttpUtil.class) {
                if (singleton == null) {
                    singleton = new OkHttpClient();
                }
            }
        }
        return singleton;
    }

    /**
     * @param url
     * @return
     */
    public static String doGet(String url) {
        final Request request = new Request.Builder().url(url).get()//默认就是GET请求，可不写
                .build();
        return execute(request);
    }

    /**
     * 提交form表单
     *
     * @param params
     */
    public static String doPost(String url, Map<String, String> params) {
        FormBody.Builder builder = new FormBody.Builder();
        params.forEach((key, value) -> builder.add(key, value));
        final RequestBody requestBody = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        return execute(request);
    }

    public static String doPost(String url, String json) {
        final RequestBody requestBody = FormBody.create(MediaType.parse("application/json;charset=utf-8"), json);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        return execute(request);
    }

    public static String doPut(String url, String requestBody, Map<String, Object> params) {
        FormBody.Builder builder = addParamToBuilder(requestBody, params);
        RequestBody body = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        return execute(request);
    }

    public static String doDelete(String url, String requestBody, Map<String, Object> map) {
        FormBody.Builder builder = addParamToBuilder(requestBody, map);
        RequestBody body = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .delete(body)
                .build();
        return execute(request);
    }


    public static FormBody.Builder addParamToBuilder(String requestBody, Map<String, Object> map) {
        FormBody.Builder builder = new FormBody.Builder();
        if (!StringUtils.isEmpty(requestBody)) {
            if (requestBody.startsWith("?")) {
                requestBody = requestBody.substring(1);
            }
            String[] params = requestBody.split("&");
            for (int i = 0; i < params.length; i++) {
                if (params[i].equals("")) {
                    continue;
                }
                String[] kv = params[i].split("=");
                builder.add(kv[0], kv[1]);
            }
        }
        if (map != null) {
            Iterator<Map.Entry<String, Object>> ite = map.entrySet().iterator();
            for (; ite.hasNext(); ) {
                Map.Entry<String, Object> kv = ite.next();
                builder.add(kv.getKey(), kv.getValue().toString());
            }
        }
        return builder;
    }

    public static String execute(Request request) {
        final OkHttpClient okHttpClient = getInstance();
        final Call call = okHttpClient.newCall(request);
        try {
            final Response response = call.execute();
            final int code = response.code();
            final String message = response.message();
            if (HttpStatus.SC_OK != code) {
                log.error("code=" + code + ",message=" + message);
                throw ZnvException.error(CommonConstant.StatusCode.INTERNAL_ERROR, "SendRequestFailed", message);
            }
            return response.body().string();
        } catch (IOException e) {
            log.error("send request failed:", e);
            throw ZnvException.error(CommonConstant.StatusCode.INTERNAL_ERROR, "SendRequestFailed", e.getMessage());
        }
    }
}
