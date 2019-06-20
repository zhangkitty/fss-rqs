package com.znv.fssrqs.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created by dongzelong on  2019/6/19 9:08.
 *
 * @author dongzelong
 * @version 1.0
 * @Description 缓冲地址配置
 */
@Configuration
@ConfigurationProperties
@Data
public class MbusConfig {
    private String ipp;
}
