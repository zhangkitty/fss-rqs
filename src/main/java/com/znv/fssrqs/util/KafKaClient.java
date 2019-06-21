package com.znv.fssrqs.util;

import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.kafka.ProducerBase;
import lombok.extern.slf4j.Slf4j;

/**
 * kafka客户端.
 * 
 * @author xkh
 */
@Slf4j
public final class KafKaClient {

    /** The Constant KAFAK_BASIC_PROPERTIES. */
    // private static final String KAFAK_BASIC_PROPERTIES = "producerBasic.properties";

    /** The pb. */
    private ProducerBase pb = new ProducerBase();

    private static KafKaClient instance = new KafKaClient();

    public static KafKaClient getInstance() {
        return instance;
    }

    private KafKaClient() {
    }

    /**
     * Inits the.
     * 
     * @throws Exception the exception
     */
    public void init() {
        log.info("now init kafka producer base");
        try {
            pb.initWithConfig(ConfigManager.getProducerProps());
        } catch (Exception e) {
            log.error("init kafka producer base failed", e);
            System.exit(0);
        }
    }

    /**
     * Gets the pb.
     * 
     * @return the pb
     */
    public ProducerBase getPb() {
        return pb;
    }

    /**
     * 关闭.
     */
    public void close() {
        pb.close();
    }

    /**
     * 写入fss
     * 
     * @param messageObj
     */
    public void sendData(JSONObject messageObj) {
        if (log.isDebugEnabled()) {
            log.debug("message send to kafka {}", messageObj);
        }
        // messageObj.put("msg_type", ConfigManager.getString("fss.kafka.topic.analysis.msgtype"));
        pb.sendData(messageObj);
        pb.flush();
    }
}
