package com.znv.fssrqs.util;

public class RateLimiter {
    private Integer limit; // 一个周期的最大流量，个
    private long period = 1; // 限流的周期，秒

    private volatile long nextTimeStamp = 0L; // 下个周期的时间戳
    private volatile int usedLimit = 0; // 一个周期已使用的流量
    private volatile Object mutexDoNotUseDirectly;

    public RateLimiter(Integer limit) {
        this.limit = limit;
    }

    private Object mutex() {
        Object mutex = this.mutexDoNotUseDirectly;
        if (mutex == null) {
            synchronized (this) {
                mutex = this.mutexDoNotUseDirectly;
                if (mutex == null) {
                    this.mutexDoNotUseDirectly = (mutex = new Object());
                }
            }
        }
        return mutex;
    }

    public static RateLimiter create(Integer limit) {
        return new RateLimiter(limit);
    }

    private void setNextTimeStamp(long nowTimeStamp) {
        nextTimeStamp = (nowTimeStamp / 1000 + period) * 1000;
    }

    private boolean checkLimited(long nowTimeStamp) {
        if (nowTimeStamp > nextTimeStamp) {
            // 处于新的周期
            usedLimit = 0;
            setNextTimeStamp(nowTimeStamp);
        }

        if ((usedLimit + 1) > limit) {
            return false;
        } else {
            usedLimit = usedLimit + 1;
            return true;
        }
    }

    public boolean tryAcquire() {
        if (limit <= 0) {
            return true;
        }

        long nowTimeStamp = System.currentTimeMillis();
        synchronized (mutex()) {
            return checkLimited(nowTimeStamp);
        }
    }
}
