package com.znv.fssrqs.service.kafka;

import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.config.HdfsConfigManager;
import com.znv.fssrqs.constant.CommonConstant;
import com.znv.fssrqs.kafka.ProducerBase;
import com.znv.fssrqs.service.hbase.PhoenixService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.znv.fssrqs.constant.CommonConstant.NotifyKafka.NOTIFY_TOPIC_MSGTYPE;

/**
 * Created by dongzelong on  2019/8/15 16:20.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@Service
@Slf4j
public class KafkaService {

    /**
     * @param primaryId   主键
     * @param referenceId 外键
     */
    public void sendToKafka(int primaryId, String referenceId) {
        ProducerBase producer = PhoenixService.getProducer();
        long startTime = System.currentTimeMillis();
        JSONObject notifyMsg = new JSONObject();
        notifyMsg.put("msg_type", HdfsConfigManager.getString(NOTIFY_TOPIC_MSGTYPE));
        notifyMsg.put("table_name", HdfsConfigManager.getTableName(CommonConstant.PhoenixProperties.PERSON_LIST_TABLE_NAME));
        notifyMsg.put("primary_id", primaryId);
        notifyMsg.put("reference_id", referenceId);
        long currentTime = System.currentTimeMillis();
        Date timeDate = new Date(currentTime);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timeStr = sdf.format(timeDate);
        notifyMsg.put("send_time", timeStr);
        boolean ret = producer.sendData(notifyMsg);
        log.info("send to kafka return {}", ret);
        System.out.println("single-PersonListClient-ret:" + ret + ",send_time:" + timeStr);
        long endTime = System.currentTimeMillis();
        System.out.println("sendToKafka use :" + (endTime - startTime));
    }
}
