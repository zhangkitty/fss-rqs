package com.znv.fssrqs.util;

import lombok.extern.slf4j.Slf4j;

import java.security.MessageDigest;

/**
 * @author zhangcaochao
 * @Description TODO
 * @Date 2019.06.28 下午12:48
 */
@Slf4j
public class MD5Util {

    /**
     * 字符串的MD5
     *
     * @param plainText
     * @return
     */
    public static String encode(String plainText) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            return buf.toString().toUpperCase();
        } catch (Exception e) {
            log.error("", e);
        }
        return "";
    }
}
