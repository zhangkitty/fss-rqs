package com.znv.fssrqs.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created by dongzelong on  2019/6/24 14:18.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@Configuration
@ConfigurationProperties("spring.datasource.hbase")
@Data
public class HbaseConfig {
    private String jdbcUrl;
}
