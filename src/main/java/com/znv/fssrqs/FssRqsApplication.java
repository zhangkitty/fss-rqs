package com.znv.fssrqs;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
@MapperScan("com.znv.fssrqs")
public class FssRqsApplication {

	public static void main(String[] args) {

		SpringApplication.run(FssRqsApplication.class, args);
	}

}
