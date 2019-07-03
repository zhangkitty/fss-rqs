package com.znv.fssrqs.util;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

public class TimingCounter {
    private static final Map<String, Long> mapMinuteCounter = new ConcurrentHashMap<String, Long>();
    private static final Map<String, Long> mapDayCounter = new ConcurrentHashMap<String, Long>();

    private static Timer timerMinute = null;
    private static Timer timerDay = null;

    private static TimingCounter instance = new TimingCounter();

    public static TimingCounter getInstance() {
        return instance;
    }

    private TimingCounter() {
        timerMinute = new Timer();
        timerMinute.schedule(new TimerTask() {
            @Override
            public void run() {
                //计数器定时清零
                for (Map.Entry<String, Long> entry : mapMinuteCounter.entrySet()) {
                    entry.setValue(0L);
                }
            }
        }, 60000L, 60000L);

        LocalDateTime midnight = LocalDateTime.now().
                plusDays(1L).withHour(0).withMinute(0).withSecond(0).withNano(0);
        long millSeconds = ChronoUnit.MILLIS.between(LocalDateTime.now(), midnight);
        timerDay = new Timer();
        timerDay.schedule(new TimerTask()
        {
            public void run()
            {
                for (Map.Entry<String, Long> entry : TimingCounter.mapDayCounter.entrySet()) {
                    entry.setValue(Long.valueOf(0L));
                }
            }
        }, millSeconds, 86400000L);
    }

    /**
     * 判断是否流控，判断的同时计数
     * @param key
     * @param maxMinuteValue
     * @return
     */
    public int isFlowControlled(String key, Long maxMinuteValue, Long maxDayValue) {
        Long valueMinute = Long.valueOf(1L);
        if (mapMinuteCounter.containsKey(key)) {
            valueMinute = Long.valueOf(((Long)mapMinuteCounter.get(key)).longValue() + 1L);
        }
        mapMinuteCounter.put(key, valueMinute);

        Long valueDay = Long.valueOf(1L);
        if (mapDayCounter.containsKey(key)) {
            valueDay = Long.valueOf(((Long)mapDayCounter.get(key)).longValue() + 1L);
        }
        mapDayCounter.put(key, valueDay);
        if (valueDay.longValue() > maxDayValue.longValue()) {
            return -2;
        }
        if (valueMinute.longValue() > maxMinuteValue.longValue()) {
            return -1;
        }
        return 0;
    }

}
