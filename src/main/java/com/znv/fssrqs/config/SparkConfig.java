package com.znv.fssrqs.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhangcaochao
 * @Description TODO
 * @Date new Date() 下午1:40
 */

@Data
@Configuration
@ConfigurationProperties(prefix = "spark")
public class SparkConfig {

    private String shellscript;
}
