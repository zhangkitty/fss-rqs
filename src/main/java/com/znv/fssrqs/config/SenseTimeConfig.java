package com.znv.fssrqs.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Created by dongzelong on  2019/6/18 15:23.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@Configuration
@ConfigurationProperties("sensetime")
@Data
public class SenseTimeConfig {
    private List<String> staticAiUnits;
    private List<String> dynamicAiUnits;
}
