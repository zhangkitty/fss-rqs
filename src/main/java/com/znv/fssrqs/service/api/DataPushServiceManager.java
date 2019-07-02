package com.znv.fssrqs.service.api;


import com.znv.fssrqs.util.SpringContextUtil;
import com.znv.fssrqs.util.SubscribManager;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class DataPushServiceManager {

    /**
     * 该方法直接由spring 容器调用
     */

    public void init() {
        log.info("start regist push data service");
        Map<String, IDataPushService> services = SpringContextUtil.getCtx().getBeansOfType(
            IDataPushService.class);
        for (IDataPushService idp : services.values()) {
            SubscribManager.getInstance().registDataPushService(idp);
        }
    }

}
