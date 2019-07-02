package com.znv.fssrqs.util;

import com.znv.fssrqs.kafka.consumer.AlarmCustume;
import com.znv.fssrqs.service.alarmImp.FaceAlarmImpl;
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
public class StartService{

    @Value("${conf.myServer.hdfsUrl}")
    private String hdfsUrl;

    public void run(){
        log.info("********************************************************");
        log.info("*********************fss-rsq start**********************");
        log.info("********************************************************");
        log.info("********************************************************");
        log.info("********************************************************");

        // 初始化kafka组件
        KafKaClient.getInstance().init();
        log.info("Init kafka client success");

        // 初始化kafka告警消费
        AlarmCustume.initCustume();
        log.info("初始化kafka告警消费");
        AlarmCustume.registAlarmService(new FaceAlarmImpl());
    }
}
