package com.znv.fssrqs.util;

import com.alibaba.fastjson.JSONObject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PersonInfoCache {
    private static PersonInfoCache instance = new PersonInfoCache();
    private Map<String, String> mapImgCache = new ConcurrentHashMap<>();
    private Map<String, JSONObject> mapInfoCache = new ConcurrentHashMap<>();

    private final static int MAX_CACHE_SIZE = 10000;

    public static PersonInfoCache getInstance() {
        return instance;
    }

    public void cachePersonImage(String key, String value) {
        if (mapImgCache.size() > MAX_CACHE_SIZE) { // 暂不考虑线程同步
            mapImgCache.clear();
        }
        mapImgCache.put(key, value);
    }

    public void cachePersonInfo(String key, JSONObject value) {
        if (mapInfoCache.size() > MAX_CACHE_SIZE) { // 暂不考虑线程同步
            mapInfoCache.clear();
        }
        mapInfoCache.put(key, value);
    }

    public String getPersonImage(String key) {
        if (key == null) {
            return null;
        }
        return mapImgCache.get(key);
    }

    public JSONObject getPersonInfo(String key) {
        if (key == null) {
            return null;
        }
        return mapInfoCache.get(key);
    }
}