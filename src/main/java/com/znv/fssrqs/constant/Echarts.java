package com.znv.fssrqs.constant;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author zhangcaochao
 * @Description TODO
 * @Date new Date() 下午2:15
 */

@Configuration
@Data
@ConfigurationProperties(prefix = "echarts")
public class Echarts {

    private String add;
}
