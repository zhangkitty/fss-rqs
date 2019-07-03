package com.znv.fssrqs.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@ConfigurationProperties(prefix = "chongqing")
public class ChongQingConfig {
    private String edsUrl;
    private String imageUrl;
    private Long maxMinuteFlow;
    private Long maxDayFlow;
}
