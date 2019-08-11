package com.znv.fssrqs.util;

import com.znv.fssrqs.config.ServerConfig;
import com.znv.fssrqs.constant.CommonConstant;
import com.znv.fssrqs.entity.mysql.MBusEntity;
import com.znv.fssrqs.enums.ImageStoreType;
import com.znv.fssrqs.exception.ZnvException;
import com.znv.fssrqs.timer.SystemDeviceLoadTask;

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
        MBusEntity mbus = SystemDeviceLoadTask.getMBus();
        if (mbus == null) {
            throw ZnvException.error("NoMBus");
        }
        String ipp = mbus.getPrivateIP() + ":" + mbus.getPort();
        return getUrl(mappingUrl, ipp, params);
    }

}
