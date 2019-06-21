package com.znv.fssrqs.util;

import com.znv.fssrqs.config.HdfsConfigManager;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import com.znv.fssrqs.config.SenseTimeConfig;

import java.util.UUID;

/**
 * Created by dongzelong on  2019/6/5 14:19.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
public class FaceAIUnitUtils {
    public static String getImageFeature(String imageData) {
        String result = null;
        CloseableHttpClient client = HttpClientPool.getInstance().getHttpClient();
        HttpPost httpPost = new HttpPost("http://" + SpringContextUtil.getCtx().getBean(SenseTimeConfig.class) + "/verify/feature/gets");
        httpPost.setHeader(HttpHeaders.CONNECTION, "close");
        httpPost.setConfig(HttpClientPool.requestConfig());
        String flag = HdfsConfigManager.getString("sensetime.http.auth");
        if (!StringUtils.isEmpty(flag) && flag.equals("true")) {
            httpPost.setHeader("Authorization", HdfsConfigManager.getString("sensetime.http.auth.header"));
        }
        ByteArrayBody bab = new ByteArrayBody(Base64Util.decode(imageData), UUID.randomUUID().toString());
        HttpEntity entity = MultipartEntityBuilder.create().addPart("imageData", bab).build();
        httpPost.setEntity(entity);
        CloseableHttpResponse response = null;
        HttpEntity resEntity = null;
        try {
            response = client.execute(httpPost);
            resEntity = response.getEntity();
            result = EntityUtils.toString(resEntity);
        } catch (Exception e) {
            throw new RuntimeException("获取静态图片特征值失败");
        } finally {
            httpPost.releaseConnection();
        }
        return result;
    }
}
