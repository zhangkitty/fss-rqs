package com.znv.fssrqs.util;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

public class MinuteCounter {
    private static final Map<String, Long> mapCounter = new ConcurrentHashMap<String, Long>();

    private static Timer time = null;

    private static MinuteCounter instance = new MinuteCounter();

    public static MinuteCounter getInstance() {
        return instance;
    }

    private MinuteCounter() {
        time = new Timer();
        time.schedule(new TimerTask() {
            @Override
            public void run() {
                //计数器定时清零
                for (Map.Entry<String, Long> entry : mapCounter.entrySet()) {
                    entry.setValue(0L);
                }
            }
        }, 60000, 60000);
    }

    /**
     * 判断是否流控，判断的同时计数
     * @param key
     * @param maxValue
     * @return
     */
    public Boolean isFlowControlled(String key, Long maxValue) {
        if (! mapCounter.containsKey(key)){
            mapCounter.put(key, 1L);
            return (1L > maxValue);
        }

        Long value = mapCounter.get(key) + 1L;
        mapCounter.put(key, value);
        return (value > maxValue);
    }

}
