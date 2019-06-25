package com.znv.fssrqs.util;

import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.util.Properties;

/**
 * 读取properties.
 * 
 * @author xkh
 */
@Slf4j
public class FssPropertyUtils extends Properties {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The instance. */
    private static FssPropertyUtils instance = new FssPropertyUtils();

    private boolean isInit = false;

    /**
     * Gets the single instance of FssPropertyUtils.
     * 
     * @return single instance of FssPropertyUtils
     */
    public static FssPropertyUtils getInstance() {
        return instance;
    }

    /**
     * Instantiates a new fss property utils.
     */
    public FssPropertyUtils() {

    }
}
