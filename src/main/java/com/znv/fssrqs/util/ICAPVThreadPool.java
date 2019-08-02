package com.znv.fssrqs.util;

import com.sun.org.apache.bcel.internal.util.ClassPath;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.util.NamedThreadFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池.
 *
 * @author xkh
 */
@Slf4j
public final class ICAPVThreadPool {

    /**
     * The Constant THREAD_POOL_CONFIG.
     */
    private static final String THREAD_POOL_CONFIG = "threadpool.properties";
    private static ICAPVThreadPool instance = new ICAPVThreadPool();
    /**
     * The t.
     */
    private ThreadPoolExecutor t = null;
    private int maxmumPoolsize;
    private int corePoolsize;
    private long keepAliveTime;
    private int blockqueuesize;
    private String name;

    /**
     * Instantiates a new ICAPV thread pool.
     */
    private ICAPVThreadPool() {
        maxmumPoolsize = DataConvertUtils.strToInt("200");
        corePoolsize = DataConvertUtils.strToInt("100");
        keepAliveTime = DataConvertUtils.strToLong("5");
        blockqueuesize = DataConvertUtils.strToInt("20000");
        name = "ICAPVThreadpool";
        t = new ThreadPoolExecutor(corePoolsize, maxmumPoolsize, keepAliveTime, TimeUnit.SECONDS,
                new LinkedBlockingQueue(blockqueuesize));
        NamedThreadFactory fa = new NamedThreadFactory(name);
        t.setThreadFactory(fa);
        t.setRejectedExecutionHandler(new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                log.warn("one message was been discasded.");
            }
        });
    }

    /**
     * Gets the single instance of ICAPVThreadPool.
     *
     * @return single instance of ICAPVThreadPool
     */
    public static ICAPVThreadPool getInstance() {
        return instance;
    }

    public int getLeftTasks() {
        return t.getQueue().size();
    }

    /**
     * Gets the acitive thread.
     *
     * @return the acitive thread
     */
    public int getAcitiveThread() {
        return t.getActiveCount();
    }

    /**
     * Execute.
     *
     * @param run the run
     */
    public void execute(Runnable run) {
        t.execute(run);
    }

    /**
     * Shutdown.
     */
    public void shutdown() {
        t.shutdown();
    }

    public int getMaxmumPoolsize() {
        return maxmumPoolsize;
    }

    public int getCorePoolsize() {
        return corePoolsize;
    }

    public long getKeepAliveTime() {
        return keepAliveTime;
    }

    public int getBlockqueuesize() {
        return blockqueuesize;
    }

    public String getName() {
        return name;
    }
}
