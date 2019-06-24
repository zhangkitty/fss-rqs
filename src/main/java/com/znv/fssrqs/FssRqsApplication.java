package com.znv.fssrqs;

import com.znv.fssrqs.elasticsearch.index.mapper.image.FeatureFieldMapper;
import com.znv.fssrqs.elasticsearch.lopq.LOPQModel;
import org.mybatis.spring.annotation.MapperScan;
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

import java.io.IOException;

@EnableScheduling
@EnableAsync
@ComponentScan
@SpringBootConfiguration
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class, MybatisAutoConfiguration.class})
//@MapperScan(value = {"com.znv.fssrqs.dao.mysql","com.znv.fssrqs.dao.hbase"})
public class FssRqsApplication {

	public static final String LOPQ_MODEL_FILE = "/lopq/lopq_model_V1.0_D512_C36.lopq";

	public static void main(String[] args) {

		try {
			LOPQModel.loadProto(FeatureFieldMapper.class.getResourceAsStream(LOPQ_MODEL_FILE));
		} catch (IOException e) {
			e.printStackTrace();
		}

        SpringApplication application = new SpringApplication(FssRqsApplication.class);


        application.addListeners(new ApplicationEventListener());
        application.run(args);
	}

}
