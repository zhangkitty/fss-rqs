package com.znv.fssrqs.util;

import lombok.extern.slf4j.Slf4j;

import java.security.MessageDigest;

/**
 * Created by dongzelong on  2019/6/3 11:16.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@Slf4j
public class MachineCodeUtils {
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

            StringBuilder buf = new StringBuilder();
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

    public static String generate() throws Exception {
        String bs = HardWare.getHardInfo().getBaseBorderSerinumber();
        String cpu = HardWare.getHardInfo().getCpuSerinumber();
        String mac = HardWare.getHardInfo().getNetSerinumber();
        String str = String.format("%s%s%s", bs, cpu, mac);
        return encode(str);
    }
}
