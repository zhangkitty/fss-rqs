package com.znv.fssrqs.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.*;

public class CommonUtil {

    public static String map2String(Map<String, String> param) {
        StringBuilder content = new StringBuilder();
        List<String> keys = new ArrayList<String>(param.keySet());
        Collections.sort(keys);
        int count = 0;
        for (int i = 0; i < keys.size(); i++) {
            String key = (String) keys.get(i);
            if ("sign".equals(key)) {
                continue;
            }
            if (param.get(key) != null) {
                String value = param.get(key).toString();
                if (StringUtils.isNotEmpty(value)) {
                    content.append((count == 0 ? "" : "&") + key + "=" + value);
                    count++;
                }
            }
        }
        return content.toString();
    }

    public static String sign(String content, String key) {
        String sign = null;
        try {
            sign = sign(content, key, "utf-8");
        } catch (Exception e) {
        }
        return sign;
    }

    public static String sign(String content, String key, String charset) throws Exception {

        String tosign = (content == null ? "" : content) + key;

        try {
            return DigestUtils.md5Hex(getContentBytes(tosign, charset));
        } catch (UnsupportedEncodingException e) {
            throw e;
        }
    }

    protected static byte[] getContentBytes(String content, String charset) throws UnsupportedEncodingException {
        if (StringUtils.isEmpty(charset)) {
            return content.getBytes();
        }
        return content.getBytes(charset);
    }

    public static Map<String, String> objectToMap(Object param) throws IllegalAccessException {
        Map<String, String> map = new HashMap<String, String>();
        // 获取f对象对应类中的所有属性域
        Field[] fields = param.getClass().getDeclaredFields();
        for (int i = 0, len = fields.length; i < len; i++) {
            String varName = fields[i].getName();
            // 获取原来的访问控制权限
            boolean accessFlag = fields[i].isAccessible();
            // 修改访问控制权限
            fields[i].setAccessible(true);
            // 获取在对象f中属性fields[i]对应的对象中的变量
            Object o = fields[i].get(param);
            if (o != null && StringUtils.isNotBlank(o.toString().trim())) {
                map.put(varName, o.toString().trim());
                // 恢复访问控制权限
                fields[i].setAccessible(accessFlag);
            }
        }
        return map;
    }

}
