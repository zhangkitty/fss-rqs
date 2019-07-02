package com.znv.fssrqs.websocket;

import java.util.concurrent.ConcurrentHashMap;

public class WebSocketSessionContext extends ConcurrentHashMap<String, WebSocketAsSession> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private Integer mbusFlushSize;

    private static WebSocketSessionContext instance = new WebSocketSessionContext();

    public static WebSocketSessionContext getInstance() {
        return instance;
    }

    public Integer getMbusFlushSize() {
        return mbusFlushSize;
    }

    public void setMbusFlushSize(Integer mbusFlushSize) {
        this.mbusFlushSize = mbusFlushSize;
    }
}
