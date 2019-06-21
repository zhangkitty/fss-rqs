package com.znv.fssrqs.kafka;


import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ProducerState {
    private static int callCount = 0;
    private static int sendCount = 0;
    private static int succCount = 0;
    private static int faildCount = 0;
    private static int skipCount = 0;
    private static int resendCount = 0;
    private static long startTime = System.currentTimeMillis();
    private static Map<String, Integer> topicMap = new HashMap();

    public ProducerState() {
    }

    static void addTopicCount(String topic) {
        if (topicMap.containsKey(topic)) {
            topicMap.put(topic, (Integer)topicMap.get(topic) + 1);
        } else {
            topicMap.put(topic, 1);
        }

    }

    static void addCallCount() {
        ++callCount;
    }

    static void addSendCount() {
        ++sendCount;
    }

    static void addSuccCount() {
        ++succCount;
    }

    static void addFaildCount() {
        ++faildCount;
    }

    static void addSkipCount() {
        ++skipCount;
    }

    static void addResendCount() {
        ++resendCount;
    }

    public static String getState() {
        long times = (System.currentTimeMillis() - startTime) / 1000L;
        long hrs = 0L;
        long min = 0L;
        long sec = 0L;
        times = times <= 0L ? 1L : times;
        sec = times;
        if (times >= 60L) {
            min = times / 60L;
            sec = times % 60L;
        }

        if (min > 60L) {
            hrs = min / 60L;
            min %= 60L;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Producer call count: ").append(callCount);
        sb.append(", send count: ").append(sendCount);
        sb.append(", succ count: ").append(succCount);
        sb.append(", faild count: ").append(faildCount);
        sb.append(", skip count: ").append(skipCount);
        sb.append(", resend count: ").append(resendCount);
        sb.append("\nProducer call time: ").append(hrs).append("h").append(min).append("m").append(sec);
        sb.append("s, speed: ").append((long)callCount / times).append("'/s");
        sb.append("\nProducer used topic: ").append(JSON.toJSONString(topicMap));
        String ret = sb.toString();
        log.info(ret);
        return ret;
    }
}

