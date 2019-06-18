package com.znv.fssrqs.config;

import com.hikvision.artemis.sdk.config.ArtemisConfig;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhangcaochao
 * @Description TODO
 * @Date 2019.06.11 上午9:30
 */
@Configuration
@Data
@ConfigurationProperties(prefix = "hksdk")
public class HkSdkConfig {
    private boolean isHkSwitch;
    private String baseUrl;
    private String appKey;
    private String appSecret;

    @Bean
    public void hk() {
        ArtemisConfig.host = baseUrl; // 代理API网关nginx服务器ip端口
        ArtemisConfig.appKey = appKey;  // 秘钥appkey
        ArtemisConfig.appSecret = appSecret;// 秘钥appSecret
    }
}
