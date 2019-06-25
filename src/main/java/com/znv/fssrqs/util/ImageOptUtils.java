package com.znv.fssrqs.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


import com.znv.fssrqs.common.Consts;
import com.znv.fssrqs.devicemgr.MBUSServerContext;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class ImageOptUtils {
    private static final String RET_STR = "{\"result\":\"-1\"}";

    private ImageOptUtils() {
    }



    public static String getImgUrl(String remoteIp, String mappingUrl, String params) {
        boolean isPub = NetMapUtil.isPublicService(remoteIp);
        JSONObject json = MBUSServerContext.getMBUS();
        String ip = FssPropertyUtils.getInstance().getProperty("ip");
        int port = 9008;
        if (json != null) {
            ip = json.getString("private_service_addr");
            port = json.getIntValue(Consts.FinalKeyCode.HTTP_PORT);
            if (isPub) {
                ip = json.getString(Consts.FinalKeyCode.SERVICE_ADDR);
            }
        }
        return ImageUtil.getUrl(mappingUrl, ip, port, params);
    }

}
