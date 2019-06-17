package com.znv.fssrqs.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhangcaochao
 * @Description TODO
 * @Date 2019.06.11 上午11:14
 */

@Configuration
public class WebConfig {


    /**
     * Bean Util
     * @return
     */
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
