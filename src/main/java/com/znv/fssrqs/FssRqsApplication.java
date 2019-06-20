package com.znv.fssrqs;

import com.znv.fssrqs.elasticsearch.index.mapper.image.FeatureFieldMapper;
import com.znv.fssrqs.elasticsearch.lopq.LOPQModel;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.io.IOException;

@SpringBootApplication
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class,DataSourceTransactionManagerAutoConfiguration.class, MybatisAutoConfiguration.class})
public class FssRqsApplication {

	public static final String LOPQ_MODEL_FILE = "/lopq/lopq_model_V1.0_D512_C36.lopq";

	public static void main(String[] args) {

		try {
			LOPQModel.loadProto(FeatureFieldMapper.class.getResourceAsStream(LOPQ_MODEL_FILE));
		} catch (IOException e) {
			e.printStackTrace();
		}

		SpringApplication.run(FssRqsApplication.class, args);
	}

}
