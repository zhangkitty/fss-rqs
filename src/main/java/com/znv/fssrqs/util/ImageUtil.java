package com.znv.fssrqs.util;

import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.client.NginxServerUrl;
import com.znv.fssrqs.common.Consts;
import com.znv.fssrqs.enums.StoreImgType;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

@Slf4j
public class ImageUtil {


    private static int getImgStoreType() {
        return DataConvertUtils.strToInt(FssPropertyUtils.getInstance().getProperty("store_img_type"));
    }

    public static String getUrl(String mappingUrl, String ip, int port, String params) {
        // 如果是http 开头则直接返回
        if (params.startsWith("http://")) {
            return params;
        }
        switch (mappingUrl) {
            case Consts.FdfsConfig.BIG_PIC_URL:
                if (StoreImgType.HBASE.getValue() == getImgStoreType()) {
                    return String.format("http://%s:%s/%s?%s", ip, port, mappingUrl, params);
                } else {
                    if (params.contains("group")) {
                        NginxServerUrl.Nginx iddr = NginxServerUrl.getNginxServerUrl().getIddr();
                        String nginxIp = FssPropertyUtils.getInstance().getProperty("ip");
                        int nginxPort = 80;
                        if (iddr != null) {
                            nginxIp = iddr.getIp();
                            nginxPort = iddr.getPort();
                        }
                        return String.format("http://%s:%s/%s", nginxIp, nginxPort, params);
                    } else if (params.contains("fastdfs")) {
                        return String.format("http://%s:%s/GetPictureUrl/%s", ip, port, params);
                    } else if (params.contains("dhcloud")) {
                        return String.format("http://%s:%s/GetDHPicUrl/%s", ip, port, params);
                    }
                    return String.format("http://%s:%s/%s?%s", ip, port, mappingUrl, params);
                }
            case Consts.FdfsConfig.SMALL_PIC_URL:
                if (StoreImgType.FDFS.getValue() == getImgStoreType()) {
                    NginxServerUrl.Nginx iddr = NginxServerUrl.getNginxServerUrl().getIddr();
                    String nginxIp = FssPropertyUtils.getInstance().getProperty("ip");
                    int nginxPort = 80;
                    if (iddr != null) {
                        nginxIp = iddr.getIp();
                        nginxPort = iddr.getPort();
                    }
                    return String.format("http://%s:%s/%s", nginxIp, nginxPort, params);
                } else {
                    return String.format("http://%s:%s/%s?%s", ip, port, mappingUrl, params);
                }
            default:
                return String.format("http://%s:%s/%s?%s", ip, port, mappingUrl, params);
        }
    }





}