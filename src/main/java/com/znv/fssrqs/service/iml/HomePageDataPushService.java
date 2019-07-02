package com.znv.fssrqs.service.iml;

import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.service.api.IDataPushService;
import com.znv.fssrqs.websocket.WebSocketAsSession;

import lombok.extern.slf4j.Slf4j;

/**
 * 首页抓拍模块当日抓拍实时推送.
 */
@Slf4j
public class HomePageDataPushService implements IDataPushService {

    /**
     * The page name.
     */
    private String pageName = "homepage";

    /**
     * The type.
     */
    private int type = 2;

    /*
     * (non-Javadoc)
     * @see com.znv.scim.fssapp.ai.IDataPushService#service(com.znv.scim.tcp.WebSocket, com.alibaba.fastjson.JSONObject)
     */
    @Override
    public void service(WebSocketAsSession ws, JSONObject object) {
        if (object != null) {
            try {
                // String taskTrackId = String.format("%s%s", object.getString("taskIdx"),object.getString("trackIdx"));
                // System.out.println("sessionId="+ws.getId()+"||taskTrackId=" + taskTrackId + "||opTime=" +
                // object.getDate("opTime").getTime());
            } catch (Exception e) {
                log.error("", e);
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see com.znv.scim.fssapp.ai.IDataPushService#getPageName()
     */
    @Override
    public String getPageName() {
        return pageName;
    }

    /*
     * (non-Javadoc)
     * @see com.znv.scim.fssapp.ai.IDataPushService#getPushEventType()
     */
    @Override
    public int getPushEventType() {
        return type;
    }

}
