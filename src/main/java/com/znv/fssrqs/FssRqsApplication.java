package com.znv.fssrqs;

import com.znv.fssrqs.listener.ApplicationEventListener;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableAsync
@ComponentScan
@SpringBootConfiguration
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class, MybatisAutoConfiguration.class})
//@MapperScan(value = {"com.znv.fssrqs.dao.mysql","com.znv.fssrqs.dao.hbase"})
public class FssRqsApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(FssRqsApplication.class);
        application.addListeners(new ApplicationEventListener());
        application.run(args);
    }
}
