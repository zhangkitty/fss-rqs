package com.znv.fssrqs.util;

import org.apache.commons.lang.StringUtils;


/**
 * 网络映射工具
 *
 * @author 徐凯华
 */
public class NetMapUtil {
    private static final String[] PRIVATE_IP_1 = {FssPropertyUtils.getInstance().getProperty("private.ip1.start"), FssPropertyUtils.getInstance().getProperty("private.ip1.end")}; // 第一私网

    private static final String[] PRIVATE_IP_2 = {FssPropertyUtils.getInstance().getProperty("private.ip2.start"), FssPropertyUtils.getInstance().getProperty("private.ip2.end")}; // 第二私网网段

    private static final String[] PRIVATE_IP_3 = {FssPropertyUtils.getInstance().getProperty("private.ip3.start"), FssPropertyUtils.getInstance().getProperty("private.ip3.end")}; // 第三私网网段

    /**
     * 对私网进行判断
     *
     * @param tmpip
     * @param ips
     * @return
     */
    private static boolean ipInner(long tmpip, String[] ips) {
        long from = ipToLong(ips[0]);
        long to = ipToLong(ips[1]);
        if (tmpip >= from && tmpip <= to) {
            return true;
        }
        return false;
    }

    private static String[] definedPublicIP() {
        String ipPropertys = FssPropertyUtils.getInstance().getProperty("public_ip_range");
        String[] ips = null;
        if (!StringUtils.isEmpty(ipPropertys)) {
            ips = FssPropertyUtils.getInstance().getProperty("public_ip_range").split("-");
        }
        return ips;
    }

    public static boolean isPublicService(String remoteIp) {
        String[] definedIp = definedPublicIP();
        boolean ispublic = true;
        if (definedIp == null) {
            boolean isInPrivate = false;
            isInPrivate = ipInner(ipToLong(remoteIp), PRIVATE_IP_1);
            if (isInPrivate) {
                return false;
            }
            isInPrivate = ipInner(ipToLong(remoteIp), PRIVATE_IP_2);
            if (isInPrivate) {
                return false;
            }

            isInPrivate = ipInner(ipToLong(remoteIp), PRIVATE_IP_3);
            if (isInPrivate) {
                return false;
            }
        } else {
            ispublic = !ipInner(ipToLong(remoteIp), definedIp);
        }
        return ispublic;
    }

    private static long ipToLong(String ip) {
        String[] addrArray = ip.split("\\.");
        long num = 0;
        for (int i = 0; i < addrArray.length; i++) {
            int power = 3 - i;
            num += ((Integer.parseInt(addrArray[i]) % 256 * Math.pow(256, power)));
        }
        return num;
    }

}
