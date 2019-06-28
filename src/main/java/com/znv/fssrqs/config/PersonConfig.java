package com.znv.fssrqs.config;

import com.hikvision.artemis.sdk.config.ArtemisConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@ConfigurationProperties(prefix = "person")
public class PersonConfig {
    private Integer personIdLength;
    private Integer totalMaxCount;
    private Integer controlMaxCount;
}
