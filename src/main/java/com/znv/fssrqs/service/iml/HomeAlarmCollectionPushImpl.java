package com.znv.fssrqs.service.iml;

import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.service.api.IDataPushService;
import com.znv.fssrqs.websocket.WebSocketAsSession;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by dongzelong on  2019/3/28 14:25.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@Slf4j
@Service
public class HomeAlarmCollectionPushImpl implements IDataPushService {


    private static final int ALARM_TYPE = 1;
    /**
     * The page name[马拉松实时采集界面].
     */
    private String pageName = "marathonalarmdata";

    /**
     * The type.
     */
    private int type = 2;

    @Override
    public void service(WebSocketAsSession ws, JSONObject object) {
        if (object != null) {
            try {
                JSONObject ret = new JSONObject();
                //只推送告警数据
                if (object.getInteger("isAlarm") == ALARM_TYPE) {
                    ret.put("data", object);
                    log.info("Send Face Data to Marathon Alarm Data Browser...");
                    ws.send(ret.toJSONString());
                }
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
