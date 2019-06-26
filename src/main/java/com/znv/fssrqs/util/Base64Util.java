package com.znv.fssrqs.util;

/**
 * Created by dongzelong on  2019/6/17 17:36.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
import org.apache.commons.codec.binary.Base64;

import java.nio.charset.Charset;

/**
 * A pure java base64 encoder and decoder, based on source code of
 * com.sun.org.apache.xerces.internal.impl.dv.util.Base64.
 */
public class Base64Util {

    public static String encodeString(String text) {
        return encode(text.getBytes(Charset.forName("utf-8")));
    }

    /**
     * Decode and return original text. Invalid base64 content will return null.
     */
    public static String decodeString(String base64) {
        return new String(decode(base64), Charset.forName("utf-8"));
    }


    /**
     * Encodes hex octects into Base64
     * @param binaryData Array containing binaryData
     * @return Encoded Base64 array
     */
    public static String encode(byte[] binaryData) {
        return Base64.encodeBase64String(binaryData);
    }

    /**
     * Decode and return original data. Invalid base64 content will return null.
     */
    public static byte[] decode(String encoded) {
        return Base64.decodeBase64(encoded);
    }

}
