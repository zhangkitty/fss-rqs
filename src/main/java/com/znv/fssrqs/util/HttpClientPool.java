package com.znv.fssrqs.util;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * Created by dongzelong on  2019/6/5 16:15.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
public class HttpClientPool {
    private static HttpClientPool instance = new HttpClientPool();

    public static HttpClientPool getInstance() {
        return instance;
    }

    public static RequestConfig requestConfig() {
        return RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(30000)
                .setRedirectsEnabled(false).build();

    }

    public CloseableHttpClient getHttpClient() {
        CloseableHttpClient client = new DefaultHttpClient();
        return client;
    }
}
