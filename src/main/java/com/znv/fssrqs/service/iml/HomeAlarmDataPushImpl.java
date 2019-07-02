package com.znv.fssrqs.service.iml;

import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.constant.MBUSConsts;
import com.znv.fssrqs.service.api.IDataPushService;
import com.znv.fssrqs.websocket.WebSocketAsSession;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 首页布控告警推送处理.
 */
@Component
@Slf4j
public class HomeAlarmDataPushImpl implements IDataPushService {

    /*
     * (non-Javadoc)
     * @see com.znv.scim.fssapp.ai.IDataPushService#service(com.znv.scim.tcp.WebSocket, com.alibaba.fastjson.JSONObject)
     */
    @Override
    public void service(WebSocketAsSession ws, JSONObject object) {
        try {
            if (MBUSConsts.FACE_DATA_ALARM != object.getIntValue("type")) {
                return;
            }
            JSONObject ret = new JSONObject();
            ret.put("data", object);
            log.info("Send Face Data to Home Real Control Browser...");
            ws.send(ret.toJSONString());
        } catch (Exception e) {
            log.error("", e);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.znv.scim.fssapp.ai.IDataPushService#getPageName()
     */
    @Override
    public String getPageName() {
        return "homerealcontrol";
    }

    /*
     * (non-Javadoc)
     * @see com.znv.scim.fssapp.ai.IDataPushService#getPushEventType()
     */
    @Override
    public int getPushEventType() {
        return 2;
    }

}
