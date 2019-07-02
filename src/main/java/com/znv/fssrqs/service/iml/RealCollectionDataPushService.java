package com.znv.fssrqs.service.iml;

import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.service.api.IDataPushService;
import com.znv.fssrqs.websocket.WebSocketAsSession;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 实时采集数据推送
 * The Class RealCollectionDataPushService.
 */
@Component
@Slf4j
public class RealCollectionDataPushService implements IDataPushService {

    /**
     * The page name.
     */
    private String pageName = "realcollection";

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
            if (ws.tryAcquireLimiterRealcollection()) {
                try {
                    JSONObject ret = new JSONObject();
                    ret.put("data", object);
                    log.info("WebSocketSessionId=" + ws.getSession().getId() + ",Send Face Data to Real Collection Browser.");
                    ws.send(ret.toJSONString());
                } catch (Exception e) {
                    log.error("", e);
                }
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
