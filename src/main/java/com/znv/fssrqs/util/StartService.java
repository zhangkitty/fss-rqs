package com.znv.fssrqs.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Order(value = 1)
public class StartService implements ApplicationRunner {

    @Value("${conf.myServer.hdfsUrl}")
    private String hdfsUrl;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("********************************************************");
        log.info("*********************fss-rsq start**********************");
        log.info("********************************************************");
        log.info("********************************************************");
        log.info("********************************************************");

        if (!StringUtils.isEmpty(hdfsUrl)) {
            ConfigManager.init(hdfsUrl);
            ConfigManager.producerInit(hdfsUrl);
        }

        // 初始化kafka组件
        KafKaClient.getInstance().init();
        log.info("Init kafka client success");
    }
}
