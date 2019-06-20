package com.znv.fssrqs.listener;

import com.znv.fssrqs.config.HdfsConfigManager;
import com.znv.fssrqs.util.SpringContextUtil;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.ContextStoppedEvent;

/**
 * Created by dongzelong on  2019/6/18 10:17.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
public class ApplicationEventListener implements ApplicationListener {

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        // 在这里可以监听到Spring Boot的生命周期
        if (event instanceof ApplicationEnvironmentPreparedEvent) {
            System.out.println("management service start to initialize environment variable");
        } else if (event instanceof ApplicationPreparedEvent) {
            System.out.println("management service initialize finish");
        } else if (event instanceof ContextRefreshedEvent) {
            SpringContextUtil.setCtx(((ContextRefreshedEvent) event).getApplicationContext());
            System.out.println("management service refresh");
        } else if (event instanceof ApplicationReadyEvent) {
            System.out.println("management service has launched finish");
        } else if (event instanceof ContextStartedEvent) {
            System.out.println("management service launch，it need to dynamic add listener in order to capture");
        } else if (event instanceof ContextStoppedEvent) {
            System.out.println("management service has stopped");
        } else if (event instanceof ContextClosedEvent) {
            System.out.println("management service has closed");
        } else {
            return;
        }
    }
}
