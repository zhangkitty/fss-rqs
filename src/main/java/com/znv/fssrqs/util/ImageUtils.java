package com.znv.fssrqs.util;

import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.config.HdfsConfigManager;
import com.znv.fssrqs.config.MbusConfig;
import com.znv.fssrqs.config.SenseTimeConfig;
import com.znv.fssrqs.config.ServerConfig;
import com.znv.fssrqs.constant.CommonConstant;
import com.znv.fssrqs.enums.ImageStoreType;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Created by dongzelong on  2019/6/25 14:44.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
public class ImageUtils {
    private static int getImgStoreType() {
        return SpringContextUtil.getCtx().getBean(ServerConfig.class).getImageStoreType();
    }

    public static String getUrl(String mappingUrl, String ipp, String params) {
        if (params.startsWith("http://")) {
            return params;
        }
        switch (mappingUrl) {
            case CommonConstant.FdfsConfig.BIG_PIC_URL:
                if (ImageStoreType.HBASE.getCode() == getImgStoreType()) {
                    return String.format("http://%s/%s?%s", ipp, mappingUrl, params);
                } else {
                    if (params.contains("group")) {
                        NginxUtil.Nginx iddr = NginxUtil.getInstance().getIddr();
                        String nginxIp = iddr.getIp();
                        int nginxPort = iddr.getPort();
                        return String.format("http://%s:%s/%s", nginxIp, nginxPort, params);
                    } else if (params.contains("fastdfs")) {
                        return String.format("http://%s/GetPictureUrl/%s", ipp, params);
                    } else if (params.contains("dhcloud")) {
                        return String.format("http://%s/GetDHPicUrl/%s", ipp, params);
                    }
                    return String.format("http://%s/%s?%s", ipp, mappingUrl, params);
                }
            case CommonConstant.FdfsConfig.SMALL_PIC_URL:
                if (ImageStoreType.FDFS.getCode() == getImgStoreType()) {
                    NginxUtil.Nginx iddr = NginxUtil.getInstance().getIddr();
                    String nginxIp = iddr.getIp();
                    int nginxPort = iddr.getPort();
                    return String.format("http://%s:%s/%s", nginxIp, nginxPort, params);
                } else {
                    return String.format("http://%s/%s?%s", ipp, mappingUrl, params);
                }
            default:
                return String.format("http://%s/%s?%s", ipp, mappingUrl, params);
        }
    }

    public static String getImgUrl(String remoteIp, String mappingUrl, String params) {
        List<String> listMBusIpp = SpringContextUtil.getCtx().getBean(MbusConfig.class).getIpp();
        if (listMBusIpp == null || listMBusIpp.isEmpty()) {
            return null;
        }
        Random r = new Random(System.currentTimeMillis());
        String ipp = listMBusIpp.get(r.nextInt(listMBusIpp.size()));
        return getUrl(mappingUrl, ipp, params);
    }


    public static String getImageFeature(String imageData) {
        String result = null;
        CloseableHttpClient client = HttpClientPool.getInstance().getHttpClient();
        HttpPost httpPost = new HttpPost(String.format("http://%s/verify/feature/gets", SpringContextUtil.getCtx().getBean(SenseTimeConfig.class).getStaticAiUnits().get(0)));
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
            throw new RuntimeException(e);
        } finally {
            httpPost.releaseConnection();
        }
        return result;
    }
}
