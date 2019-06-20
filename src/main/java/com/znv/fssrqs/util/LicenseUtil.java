package com.znv.fssrqs.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.security.interfaces.RSAPrivateKey;

/**
 * Created by dongzelong on  2019/6/3 10:50.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@Slf4j
public class LicenseUtil {
    private static String privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAIdA+ulpU84bOxdawfeJRn8EOf/0thtliNnfeJADYmpW/N4ibxWQmC0indOLThGQQU8zBmyFDm/mgKv+59+ZuSfWy4r14LDrw1dRRFXTFnqk6bMQG/K3Ylckr/9OCYuAmAb/zelfvz1HDSM17zTtcRJ71ThV9PfN3W4cdhM+C1ihAgMBAAECgYBruRhecPI7dLrCphlYsu//1/jt4BRui5bMmEzovplSwseRkHFTBp/9StXTF99s46zwjF4wbmUKQNb4pgQ9tayeYCYgqVA7TqHKB45eTiBPK+U0ljHoG1z9/JyHttFORGIMrfUlnRvHJUIjYQGZUKS+4T2jOLrW9a/ocpmVioPOFQJBANVnzHw1/8prIyXA/2LGL5/HcxjCIWVBcehc7uCUQTvxVFEn4keOBa1gAkXgRj6EbJp/FkQ3ht1GjweRYpr9GkcCQQCiP+6t7LkXRm+7v1cGW9n673xoQn7L4xUV+oRMq+f6iuxq/Kmv5q/tUjI4cGIUe0SmDaMwLA8HXI+qX1YGoAHXAkEAkq9JP+uCCamAvDefk040d/gJJfByMf49BIG0dEuTV1d3JF1szNBTGKvQhCU3Q0uUttE6BePA4KHaOFJ3W58ziwJAaoCC4OHxwUh4EYqQyljCpkhLpnh3mMgv0CSIcXeqJ7jHZZcCn7dSpfp9grSqfP1JW6K6CuXw24kzuMcpxeGEMwJAReM195mKJ488xQim+enebbwexrE2J5LZmtHbUqUkaKYSRuR+BMJcUL98rHNgCf/411U+HUD3Z0o9N3mwFBR7xw==";
    //任务数
    private int taskCount = 0;
    public static LicenseUtil instance = new LicenseUtil();

    public LicenseUtil() {
        getLicenseCount();
    }

    /**
     * 获取license数量
     */
    private void getLicenseCount() {
        JSONObject json = null;
        byte[] enData = null;
        byte[] deData = null;
        RSAPrivateKey rsaPrivateKey;
        try {
            enData = SynReaderAndWriter.read(System.getProperty("user.dir") + File.separator + "licence.lic");
            rsaPrivateKey = DecodeRSA.loadPrivateKeyByStr(privateKey);
            if (enData == null) {
                log.error("encrypt data is null");
            }
            deData = DecodeRSA.decrypt(rsaPrivateKey, enData);
            if (deData == null) {
                log.error("decrypt data is null");
            }
            json = JSON.parseObject(new String(deData, "utf-8"));
            String machineCodeStr = MachineCodeUtils.generate();
            if (StringUtils.isEmpty(machineCodeStr)) {
                throw new Exception("gen machineCode is null");
            }
            if (json != null) {
                taskCount = json.getIntValue(machineCodeStr);
            }
        } catch (Exception e) {
            taskCount = 0;
            log.warn("read licence.lic fail", e);
        }
        log.info("license count:" + taskCount);
    }

    public int getTaskCount() {
        return taskCount;
    }
}
