package com.znv.fssrqs.devicemgr;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.http.ParseException;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
public class MBUSServerContext {
    private static List<JSONObject> mbuses = new CopyOnWriteArrayList<JSONObject>();

    private static List<JSONObject> online = new CopyOnWriteArrayList<JSONObject>();;

    private static List<JSONObject> unline = new CopyOnWriteArrayList<JSONObject>();;

    private static Timer t = null;

    private static Integer isMbusOnExternalNetwork = new Integer(0);

    public static JSONObject getMBUS() {
        if (online.isEmpty()) {
            return null;
        }
        Random r = new Random(System.currentTimeMillis());
        int i = r.nextInt(online.size());
        return online.get(i);
    }

    public static List<JSONObject> getMBUSServers() {
        return mbuses;
    }
}
