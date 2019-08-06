package com.znv.fssrqs.listener;

import com.znv.fssrqs.support.I18nHelper;
import com.znv.fssrqs.util.I18nUtils;
import com.znv.fssrqs.util.SpringContextUtil;
import com.znv.fssrqs.util.StartService;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class ApplicationEventListener implements ApplicationListener {

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        // 在这里可以监听到Spring Boot的生命周期
        if (event instanceof ApplicationEnvironmentPreparedEvent) {
            log.info("management service start to initialize environment variable");
        } else if (event instanceof ApplicationPreparedEvent) {
            log.info("management service initialize finish");
        } else if (event instanceof ContextRefreshedEvent) {
            SpringContextUtil.setCtx(((ContextRefreshedEvent) event).getApplicationContext());
            final I18nHelper i18nHelper = SpringContextUtil.getCtx().getBean(I18nHelper.class);
            I18nUtils.setHolder(i18nHelper);
            StartService startService = SpringContextUtil.getCtx().getBean(StartService.class);
            startService.run();
            log.info("management service refresh");
        } else if (event instanceof ApplicationReadyEvent) {
            log.info("management service has launched finish");
        } else if (event instanceof ContextStartedEvent) {
            log.info("management service launch，it need to dynamic add listener in order to capture");
        } else if (event instanceof ContextStoppedEvent) {
            log.info("management service has stopped");
        } else if (event instanceof ContextClosedEvent) {
            log.info("management service has closed");
        } else {
            return;
        }
    }
}
