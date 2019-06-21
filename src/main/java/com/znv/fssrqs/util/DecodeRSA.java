package com.znv.fssrqs.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

/**
 * Created by dongzelong on  2019/6/3 11:12.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@Data
@Slf4j
public class DecodeRSA {

    public static final String PATH = "D:\\Users\\User\\Desktop";

    /**
     * byte数组转为string
     *
     * @param encrytpByte
     * @return
     */
    public static String bytesToString(byte[] encrytpByte) {
        String result = "";
        for (Byte bytes : encrytpByte) {
            result += (char) bytes.intValue();
        }
        return result;
    }

    public static String loadPrivateKeyByFile(String path) throws Exception {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(path
                    + "/privateKey.keystore"));
            String readLine = null;
            StringBuilder sb = new StringBuilder();
            while ((readLine = br.readLine()) != null) {
                sb.append(readLine);
            }
            return sb.toString();
        } catch (IOException e) {
            throw new Exception("私钥数据读取错误");
        } catch (NullPointerException e) {
            throw new Exception("私钥输入流为空");
        } finally {
            if (br != null) {
                br.close();
            }
        }
    }

    public static RSAPrivateKey loadPrivateKeyByStr(String privateKeyStr)
            throws Exception {
        try {
            byte[] buffer = Base64Util.decode(privateKeyStr);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此算法");
        } catch (InvalidKeySpecException e) {
            throw new Exception("私钥非法");
        } catch (NullPointerException e) {
            throw new Exception("私钥数据为空");
        }
    }

    public static void main(String[] args) throws Exception {
        String privateKey = loadPrivateKeyByFile(PATH);
        byte[] enData = SynReaderAndWriter.read(PATH + "/licence.lic");
        RSAPrivateKey rsaPrivateKey = DecodeRSA.loadPrivateKeyByStr(privateKey);
        if (enData == null) {
            log.error("encrypt data is null");
        }
        byte[] deData = DecodeRSA.decrypt(rsaPrivateKey, enData);
        if (deData == null) {
            log.error("decrypt data is null");
        }
        JSONObject json = JSON.parseObject(
                new String(deData, "utf-8"));


    }

    public static byte[] decrypt(RSAPrivateKey privateKey, byte[] obj) {
        if (privateKey != null) {
            try {
                Cipher cipher = Cipher.getInstance("RSA");
                cipher.init(Cipher.DECRYPT_MODE, privateKey);
                return cipher.doFinal(obj);
            } catch (Exception e) {
                log.error("", e);
            }
        }
        return null;
    }
}
