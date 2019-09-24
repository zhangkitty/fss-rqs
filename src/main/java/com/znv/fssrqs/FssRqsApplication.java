package com.znv.fssrqs;

import com.znv.fssrqs.elasticsearch.index.mapper.image.FeatureFieldMapper;
import com.znv.fssrqs.elasticsearch.lopq.LOPQModel;
import com.znv.fssrqs.listener.ApplicationEventListener;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.MultipartAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;

@EnableScheduling
@EnableAsync
//@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 1800)
@ComponentScan
@SpringBootConfiguration
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class, MybatisAutoConfiguration.class, SecurityAutoConfiguration.class, MultipartAutoConfiguration.class})
@ServletComponentScan
@Slf4j
//@NacosPropertySource(dataId = "fss-rqs", autoRefreshed = true)
//@EnableDiscoveryClient
//@NacosConfigurationProperties(dataId = "alibaba-nacos-config-client",autoRefreshed = true)
public class FssRqsApplication {
    public static final String LOPQ_MODEL_FILE = "/lopq/lopq_model_V1.0_D512_C36.lopq";

    public static void main(String[] args) {
        try {
            LOPQModel.loadProto(FeatureFieldMapper.class.getResourceAsStream(LOPQ_MODEL_FILE));
        } catch (IOException e) {
            log.error("", e);
        }
        SpringApplication application = new SpringApplication(FssRqsApplication.class);
        application.addListeners(new ApplicationEventListener());
        application.run(args);
    }

    public void init() {
        int a = 1;
        Integer b = new Integer(1);
    }
}
