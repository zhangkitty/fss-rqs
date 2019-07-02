package com.znv.fssrqs.service.iml;

import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.service.api.IDataPushService;

import com.znv.fssrqs.websocket.WebSocketAsSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 人证合一比对推送处理.
 */
@Component
@Slf4j
public class PersonCollectionDataPushService implements IDataPushService {

    /**
     * The page name.
     */
    private String pageName = "idcardandperson";

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
            String address = "";
            object.put("addr", address);
            try {
                JSONObject ret = new JSONObject();
                ret.put("data", object);
                log.info("Send Face Data to ID CARD and PERSON Browser...");
                ws.send(ret.toJSONString());
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
