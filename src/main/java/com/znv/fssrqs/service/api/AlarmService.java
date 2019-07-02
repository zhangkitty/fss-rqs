package com.znv.fssrqs.service.api;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

public interface AlarmService {
    void service(List<JSONObject> json);
}
