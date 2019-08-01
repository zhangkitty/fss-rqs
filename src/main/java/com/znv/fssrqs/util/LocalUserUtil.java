package com.znv.fssrqs.util;


import com.alibaba.fastjson.JSONObject;

public class LocalUserUtil {
    public static ThreadLocal<JSONObject> localUser = new ThreadLocal();

    public static JSONObject getLocalUser(){
        return localUser.get();
    }

    public static void setLocalUser(JSONObject user) {
        localUser.set(user);
    }

    public static void removeLocalUser() {
        localUser.remove();
    }
}
