package com.znv.fssrqs.config;

import com.hikvision.artemis.sdk.config.ArtemisConfig;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhangcaochao
 * @Description TODO
 * @Date 2019.06.11 上午9:30
 */

@Configuration
@Data
public class HKConfig {

    @Value("${hksdk.baseUrl}")
    private String baseUrl;

    @Value("${hksdk.appKey}")
    private String appKey;

    @Value("${hksdk.appSecret}")
    private String appSecret;


    @Bean
    public void hk(){
        ArtemisConfig.host = baseUrl; // 代理API网关nginx服务器ip端口
        ArtemisConfig.appKey = appKey;  // 秘钥appkey
        ArtemisConfig.appSecret = appSecret;// 秘钥appSecret
    }
}
