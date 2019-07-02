package com.znv.fssrqs.util;

import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.service.api.IDataPushService;
import com.znv.fssrqs.websocket.WebSocketAsSession;
import com.znv.fssrqs.websocket.WebSocketSessionContext;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 订阅管理.
 *
 * @author hjn
 */
public final class SubscribManager {

    /**
     * 根据摄像头ID订阅.
     */
    public static final int SUBSCRIBE_CAMERA = 1;

    /**
     * 根据局站ID订阅.
     */
    public static final int SUBSCRIBE_OFFICE = 2;

    /**
     * The Constant SUBSCRIB_TYPE.
     */
    public static final String SUBSCRIBE_TYPE = "subscribe_type";
    public static final String SUBSCRIBE_VALUE = "subscribe_value";
    private List<IDataPushService> iservices = new ArrayList<IDataPushService>();

    private static class SingletonHolder {
        private static SubscribManager instance = new SubscribManager();
    }

    public static SubscribManager getInstance() {
        return SingletonHolder.instance;
    }

    public void registDataPushService(IDataPushService ips) {
        iservices.add(ips);
    }

    public List<IDataPushService> getIservices() {
        return iservices;
    }

    public void unregistAll() {
        iservices.clear();
    }

    /**
     * Subscrib.
     *
     * @param type    the type
     * @param ids     0098,00877e2,323,id 以逗号隔开
     * @param session the session
     */
    public void subscrib(int subscribeType, String subscribeValue, WebSocketAsSession session) {
        session.put(SUBSCRIBE_TYPE, subscribeType);
        if (!StringUtils.isEmpty(subscribeValue)) {
            session.put(SUBSCRIBE_VALUE, subscribeValue);
        }
        WebSocketSessionContext.getInstance().put(session.getSession().getId(), session);
    }

    /**
     * 是否订阅
     *
     * @param object
     * @return
     */
    public boolean isSubscrib(JSONObject object, WebSocketAsSession session) {
        String paramName = "";
        int subscribeType = Integer.parseInt(String.valueOf(session.get(SUBSCRIBE_TYPE)));
        String subscribeValue = session.get(SUBSCRIBE_VALUE) == null ? "" : String.valueOf(session.get(SUBSCRIBE_VALUE));
        if (subscribeType == SUBSCRIBE_CAMERA) {
            paramName = "cameraId";
        } else if (subscribeType == SUBSCRIBE_OFFICE) {
            paramName = "officeId";
        } else {
            return true;
        }
        String id = object.getString(paramName);
        if (subscribeValue.indexOf(id) >= 0) {
            return true;
        }
        return false;
    }
}
