package com.znv.fssrqs.service.api;

import com.alibaba.fastjson.JSONObject;


/**
 * 数据清洗接口
 *
 * @author xkh
 */
public interface DataWash {

    void startWash();

    void stopWash();

    String getRealTaskId();

    void setRealTaskId(String realTaskId);

    void put(JSONObject data);

    int queueSize();
}
