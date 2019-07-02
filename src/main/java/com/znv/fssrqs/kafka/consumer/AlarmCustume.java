package com.znv.fssrqs.kafka.consumer;

import com.alibaba.fastjson.JSONObject;

import com.znv.fssrqs.service.api.AlarmService;
import com.znv.fssrqs.util.ConfigManager;
import com.znv.fssrqs.util.DataConvertUtils;
import com.znv.fssrqs.util.FssPropertyUtils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 告警消费模式.
 *
 * @author xkh
 */
@Slf4j
public final class AlarmCustume implements Runnable {

    /** The consumer. */
    private KafkaConsumer<String, Map<String, Object>> consumer;

    /** The topic. */
    private ArrayList<String> topic;

    /** The alarmservic. */
    private static List<AlarmService> alarmservic = new ArrayList<AlarmService>();

    /** The is run. */
    private boolean isRun = true;

    public static void registAlarmService(AlarmService as) {
        alarmservic.add(as);
    }

    public static void destroy() {
        alarmservic.clear();
        for (AlarmCustume ac : threads) {
            ac.consumer.close();
            ac.isRun = false;
        }
        threads.clear();
    }

    private static List<AlarmCustume> threads = new ArrayList<AlarmCustume>();

    /**
     * Instantiates a new alarm custume.
     */
    private AlarmCustume() {
    }

    public static void initCustume() {
        int count = DataConvertUtils.strToInt(FssPropertyUtils.getInstance().getProperty("alarm.custume.count", "3"));
        Thread t = null;
        for (int i = 0; i < count; i++) {
            AlarmCustume ac = new AlarmCustume();
            ac.init();
            t = new Thread(ac, "Alarm-Custume-Thread" + i);
            threads.add(ac);
            t.start();
        }

    }

    /**
     * Inits the.
     */
    private void init() {
        log.info("init alarm customus.");
        String blackTopic = "fss-alarm-v1-2-1-production";
        ArrayList<String> topicList = new ArrayList<String>();
        if (!StringUtils.isEmpty(blackTopic)) {
            String[] ts = blackTopic.split(",");
            for (String top : ts) {
                topicList.add(top);
            }
        }
        this.topic = topicList;
        Properties props = ConfigManager.getBlackAlarmProperties();
        this.consumer = new KafkaConsumer<String, Map<String, Object>>(props);
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        consumer.subscribe(this.topic);
        while (isRun) {
            try {
                ConsumerRecords<String, Map<String, Object>> records = consumer.poll(100);
                List<JSONObject> array = new ArrayList<JSONObject>();
                for (ConsumerRecord<String, Map<String, Object>> record : records) {
                    JSONObject json = new JSONObject();
                    Map<String, Object> map = record.value();
                    log.info("poll map:{}", map);
                    for (Map.Entry<?, ?> entry : map.entrySet()) {
                        String key = String.valueOf(entry.getKey());
                        if ("rt_feature".equals(key) || "rt_image_data".equals(key) || "rt_image_data2".equals(key)
                            || "rt_image_data3".equals(key)) {
                            continue;
                        }
                        json.put(parserData(entry), String.valueOf(entry.getValue()));
                    }
                    array.add(json);
                }
                consumer.commitAsync();
                for (AlarmService as : alarmservic) {
                    as.service(array);
                }

            } catch (Exception e) {
                log.error("", e);
            }
        }
    }

    private String parserData(Map.Entry<?, ?> entry) {
        String strKey = "";
        String key = String.valueOf(entry.getKey());
        switch (key) {
            case "enter_time":
                strKey = "timeStamp";
                break;
            case "rt_feature":
                strKey = "feature";
                break;
            case "rt_image_data":
                strKey = "whiteImageData";
                break;
            case "rt_image_data2":
                strKey = "whiteImageData2";
                break;
            case "rt_image_data3":
                strKey = "whiteImageData3";
                break;
            case "person_id":
                strKey = "fcPid";
                break;
            case "lib_id":
                strKey = "controlLevel";
                break;
            default:
                char[] chars = key.toCharArray();
                int j = 0;
                for (int i = 0; i < chars.length; i++) {
                    if (chars[i] == '_') {
                        chars[j++] = (char) ((int) chars[i + 1] - 32);
                        ++i;
                    } else {
                        chars[j++] = chars[i];
                    }
                }
                char[] tmpChars = new char[j];
                for (int i = 0; i < j; i++) {
                    tmpChars[i] = chars[i];
                }
                strKey = new String(tmpChars);
                break;
        }
        return strKey;
    }

}
