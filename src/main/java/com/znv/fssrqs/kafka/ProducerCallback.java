package com.znv.fssrqs.kafka;


import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.RecordMetadata;

@Slf4j
public class ProducerCallback implements Callback {
    public static final int MAX_RESEND_COUNT = 10;
    public static final int DEFAULT_RESEND_COUNT = 1;
    private int resendTimes;
    private JSONObject record;
    private ProducerBase parentProducer;

    public ProducerCallback(ProducerBase producer, JSONObject json) {
        this(producer, json, 1);
    }

    public ProducerCallback(ProducerBase producer, JSONObject json, int resendCount) {
        this.resendTimes = 1;
        this.record = null;
        this.parentProducer = null;
        this.parentProducer = producer;
        this.record = json;
        this.resendTimes = Math.min(resendCount, 10);
    }

    public void onCompletion(RecordMetadata data, Exception ex) {
        if (data == null) {
            if (ex != null) {
                log.error(ex.getMessage(), ex);
            }

            if (this.resendTimes > 0) {
                try {
                    ProducerState.addResendCount();
                    --this.resendTimes;
                    this.parentProducer.sendData(this.record, this);
                } catch (Exception var4) {
                    log.error(var4.getMessage(), var4);
                }
            } else {
                ProducerState.addFaildCount();
            }
        } else {
            ProducerState.addSuccCount();
        }

    }
}

