package com.znv.fssrqs.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created by dongzelong on  2019/6/18 16:35.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@Configuration
@ConfigurationProperties("ms")
@Data
public class ServerConfig {
    //图片存储类型
    private int imageStoreType;
}
