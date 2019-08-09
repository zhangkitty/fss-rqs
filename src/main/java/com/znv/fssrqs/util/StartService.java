package com.znv.fssrqs.util;

import com.znv.fssrqs.kafka.consumer.AlarmCustume;
import com.znv.fssrqs.timer.CompareTaskLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StartService {
    public void run() {
        log.info("********************************************************");
        log.info("*********************fss-rsq start**********************");
        log.info("********************************************************");
        log.info("********************************************************");
        log.info("********************************************************");

        // 初始化kafka组件
        KafKaClient.getInstance().init();
        log.info("init kafka client success");

        // 初始化kafka告警消费
        AlarmCustume.initCustume();
        log.info("init kafka alarm consume");

        // 初始化Sim
        SimUtil.init();

        //n:n任务加载
        CompareTaskLoader.getInstance().loadCompareTask();
    }
}
