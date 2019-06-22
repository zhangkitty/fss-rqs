package com.znv.fssrqs.kafka;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

@Slf4j
public class  ProducerBase {
    public static final String MSG_TYPE = "msg_type";
    protected KafkaProducer<String, JSONObject> producer = null;
    protected boolean isInit = false;
    private String topicSub = "production";
    private boolean isResend = false;
    //private Map<String, TopicCommond> topicMap = new HashMap();

    public ProducerBase() {
    }

    public void init() throws Exception {
        this.init((String)null, false);
    }

    public void initWithConfig(Properties props) throws Exception {
        this.init((String)null, false, props);
    }

    public void init(String topicSub) throws Exception {
        this.init(topicSub, false);
    }

    void init(String topicSub, boolean isResend) throws Exception {
        this.init(topicSub, isResend, (Properties)null);
    }

    void init(String topicSub, boolean isResend, Properties props) throws Exception {
        log.info("Producer init(topicSub: {}, isResend: {})... ", topicSub, isResend);
        if (!this.isInit) {
            File configFile = null;
            if (props == null) {
                props = new Properties();
                configFile = new File("producer.properties");
                log.info("config file path: {}", configFile.getAbsolutePath());

                try {
                    InputStream in = new BufferedInputStream(new FileInputStream(configFile));
                    Throwable var6 = null;

                    try {
                        props.load(in);
                    } catch (Throwable var16) {
                        var6 = var16;
                        throw var16;
                    } finally {
                        if (in != null) {
                            if (var6 != null) {
                                try {
                                    in.close();
                                } catch (Throwable var15) {
                                    var6.addSuppressed(var15);
                                }
                            } else {
                                in.close();
                            }
                        }

                    }
                } catch (Exception var18) {
                    throw var18;
                }
            }

            if (topicSub != null && !topicSub.equals("")) {
                this.topicSub = topicSub;
            }

            log.info("update topicSub to: {}", this.topicSub);
            this.isResend = isResend;
            this.producer = new KafkaProducer(props);
            this.isInit = true;
        }

        log.info("Producer init end.");
    }

    public void setMsgTypeParam(String msgType, String zk, int partitionNum, int replicationNum) {
        if (!this.isInit) {
            log.error("");
        } else {
            String topic = null;
            if (msgType.endsWith(this.topicSub)) {
                topic = msgType;
            } else {
                topic = this.getTopic(msgType);
            }

            //log.info("set msgType [{}] param: {}, {}, {}, {}", new Object[]{msgType, topic, zk, partitionNum, replicationNum});
            //this.topicMap.put(topic, new TopicCommond(topic, zk, partitionNum, replicationNum));
        }
    }

    String getTopic(String msgType) {
        return String.format("%s-%s", msgType, this.topicSub);
    }

    public boolean sendData(Map<String, Object> data) {
        return this.sendData(new JSONObject(data));
    }

    public boolean sendData(JSONObject data) {
        return this.sendData((String)null, (JSONObject)data);
    }

    public boolean sendData(String key, Map<String, Object> data) {
        return this.sendData(key, new JSONObject(data));
    }

    public boolean sendData(String key, JSONObject data) {
        ProducerState.addCallCount();
        if (!data.containsKey("msg_type")) {
            log.warn("msg_type is null, skip!!!");
            ProducerState.addSkipCount();
            return false;
        } else {
            String msgType = data.getString("msg_type");
            if (msgType != null && !msgType.trim().equals("")) {
                ProducerCallback callback = null;
                if (this.isResend) {
                    callback = new ProducerCallback(this, data);
                }

                try {
                    return this.sendData(key, data, callback);
                } catch (Exception var5) {
                    if (this.isResend) {
                        callback.onCompletion((RecordMetadata)null, var5);
                    } else {
                        log.error(var5.getMessage(), var5);
                    }

                    return false;
                }
            } else {
                log.warn("msg_type is null, skip!!!");
                ProducerState.addSkipCount();
                return false;
            }
        }
    }

    boolean sendData(JSONObject record, Callback callback) throws Exception {
        return this.sendData((String)null, (String)null, record, callback);
    }

    boolean sendData(String key, JSONObject record, Callback callback) throws Exception {
        return this.sendData((String)null, key, record, callback);
    }

    boolean sendData(String topic, String key, JSONObject record, Callback callback) throws Exception {
        if (!this.isInit) {
            log.warn("init Producer on send data!!!");
            this.init(this.topicSub, false);
        }

        if (this.producer == null) {
            log.error("Producer is null, skip!!!");
            ProducerState.addSkipCount();
            return false;
        } else {
            if (topic == null || topic.equals("")) {
                topic = this.getTopic(record.getString("msg_type"));
                ProducerState.addTopicCount(topic);
            }

            //if (this.topicMap.containsKey(topic)) {
            //    ((TopicCommond)this.topicMap.get(topic)).createTopic();
            //}

            ProducerRecord<String, JSONObject> data = new ProducerRecord(topic, key, record);
            if (callback != null) {
                this.producer.send(data, callback);
            } else {
                this.producer.send(data);
            }

            ProducerState.addSendCount();
            return true;
        }
    }

    public void flush() {
        this.producer.flush();
    }

    public void close() {
        if (this.isInit) {
            log.info("Producer close...");
            this.producer.close();
            this.isInit = false;
            log.info("Producer close end.");
        }

    }
}
