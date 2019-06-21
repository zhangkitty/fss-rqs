package com.znv.fssrqs.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Created by dongzelong on  2019/6/18 16:29.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@Configuration
@ConfigurationProperties("fdfs")
@Data
public class FastDfsConfig {
    private List<String> trackers;
}
