package com.znv.fssrqs.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Created by dongzelong on  2019/6/19 9:58.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@Slf4j
public class HttpUtils {
    /**
     * Send post data.
     *
     * @param data the data
     * @param url  the url
     * @return the string
     * @throws Exception
     * @throws ClientProtocolException the client protocol exception
     * @throws IOException             Signals that an I/O exception has occurred.
     */
    public static String sendPostData(String data, String url) throws ClientProtocolException, IOException {
        HttpPost post = new HttpPost(url);
        post.setHeader(HttpHeaders.CONNECTION, "close");
        post.setConfig(HttpClientPool.requestConfig());
        StringEntity se = new StringEntity(data, "utf-8");
        post.setEntity(se);
        CloseableHttpResponse response;
        try {
            response = HttpClientPool.getInstance().getHttpClient().execute(post);
            return EntityUtils.toString(response.getEntity(), "utf-8");
        } catch (Exception e) {
            throw e;
        } finally {
            post.releaseConnection();
        }

    }

    public static String sendGet(String url) throws ParseException, IOException {
        HttpGet get = new HttpGet(url);
        get.setHeader("Content-Type", "charset=utf-8");
        get.setHeader(HttpHeaders.CONNECTION,"close");
        get.setConfig(HttpClientPool.requestConfig());
        CloseableHttpResponse chc = null;
        try {
            chc = HttpClientPool.getInstance().getHttpClient().execute(get);
            return EntityUtils.toString(chc.getEntity(), "utf-8");
        } catch (Exception e) {
            throw e;
        } finally {
            get.releaseConnection();
        }
    }

    public static String postJsonString(String url, String param) {
        CloseableHttpClient httpClient = HttpClientPool.getInstance().getHttpClient();
        HttpPost post = new HttpPost(url);
        post.setHeader("content-type", "application/json;charset=utf-8");
        post.setHeader("accept", "application/json");
        post.setConfig(HttpClientPool.requestConfig());
        post.setHeader(HttpHeaders.CONNECTION, "close");
        // json字符串以实体的实行放到post中
        post.setEntity(new StringEntity(param, Charset.forName("utf-8")));
        try {
            CloseableHttpResponse response = httpClient.execute(post);
            if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
                return EntityUtils.toString(response.getEntity());
            }
        } catch (Exception e) {
            log.error("postJsonString error", e);
        } finally {
            post.releaseConnection();
        }
        return "";
    }
}
