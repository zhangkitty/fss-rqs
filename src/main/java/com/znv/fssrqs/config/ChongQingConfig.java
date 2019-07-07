package com.znv.fssrqs.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

@Configuration
@Data
@ConfigurationProperties(prefix = "chongqing")
public class ChongQingConfig {
    private String edsUrl;
    private String imageUrl;
    private Long maxMinuteFlow;
    private Long maxDayFlow;
    private String[] outerLibIds;


    private HashMap<String, String> outerLibIdsMap;
    @Bean
    public void setOuterLibIdsMap() {
        outerLibIdsMap = new HashMap<>();
        for (int i = 0; i < outerLibIds.length; i++) {
            outerLibIdsMap.put(outerLibIds[i], outerLibIds[i]);
        }
    }
}
