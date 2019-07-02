package com.znv.fssrqs.service.api;

import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.websocket.WebSocketAsSession;

/**
 * The Interface IDataPushService.
 */
public interface IDataPushService {

    /**
     * Service.
     *
     * @param ws
     * @param object
     */
    void service(WebSocketAsSession ws, JSONObject object);

    String getPageName();

    int getPushEventType();
}
