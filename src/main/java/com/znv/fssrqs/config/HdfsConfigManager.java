package com.znv.fssrqs.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dongzelong on  2019/6/17 17:42.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@Slf4j
@org.springframework.context.annotation.Configuration
@ConfigurationProperties("bigdata")
@Data
public class HdfsConfigManager {
    private static final Pattern PATTERN = Pattern.compile("\\$\\{([^\\}]+)\\}"); // 正则匹配
    //大数据相关配置
    private static Properties properties = new Properties();
    //phoenix相关配置
    private static Properties phoenixProps = new Properties();
    //kafka生产者相关配置
    private static Properties kafkaProducerProps = new Properties();
    private String hdfsUrl;

    @PostConstruct
    public void init() {
        //initFssProperties();
        //initKafkaProducer();
        //initPhoenix();
    }

    private void initFssProperties() {
        Configuration conf = new Configuration();
        FSDataInputStream in = null;
        String configPath = hdfsUrl + "/config/fss.properties";
        String hdfsFilPath = hdfsUrl + "/config";
        try {
            FileSystem fs = FileSystem.get(URI.create(configPath), conf);
            in = fs.open(new Path(configPath));
            properties.load(in);
            properties.setProperty("hdfs_url", hdfsUrl);
            properties.setProperty("hdfsFilePath", hdfsFilPath);
        } catch (IOException e) {
            log.error("read hdfs config failed {}", e);
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    log.error("close input stream failed {}", e);
                }
            }
        }
    }

    /**
     * 初始化Kafka生产者相关配置
     */
    private void initKafkaProducer() {
        String producerPath = hdfsUrl + "/config/producerBasic.properties";
        Configuration conf = new Configuration();
        FSDataInputStream in = null;
        try {
            FileSystem fs = FileSystem.get(URI.create(producerPath), conf);
            in = fs.open(new Path(producerPath));
            kafkaProducerProps.load(in);
        } catch (IOException e) {
            log.error("read producerBasic.properties config failed {}", e);
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    log.error("close  producerBasic.properties failed {}", e);
                }
            }
        }
    }

    /**
     * 读取Phoenix连接池配置
     */
    private void initPhoenix() {
        InputStream inputStream = null;
        String poolConfigPath = "/phoenix.properties";
        try {
            inputStream = HdfsConfigManager.class.getResourceAsStream(poolConfigPath);
            phoenixProps.load(inputStream);
        } catch (IOException e) {
            log.error("read phoenix.properties failed {}", e);
        } finally {
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error("close input stream failed {}", e);
                }
            }
        }
    }

    /**
     * @param key
     * @return
     */
    public static String getPhoenixConnPoolString(String key) {
        String value = phoenixProps.getProperty(key);
        Matcher matcher = PATTERN.matcher(value);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            String matcherKey = matcher.group(1);
            String matchervalue = phoenixProps.getProperty(matcherKey);
            if (matchervalue != null) {
                matcher.appendReplacement(buffer, matchervalue);
            }
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    /**
     * @param key
     * @return
     */
    public static int getPhoenixConnPoolInt(String key) {
        return Integer.parseInt(getPhoenixConnPoolString(key));
    }

    /**
     * @param key 获取Properties对象中key对应value, 并替换value中引用变量的部分为实际的值
     * @return
     */
    public static String getString(String key) {
        String value = properties.getProperty(key);
        if (StringUtils.isEmpty(value)) {
            return "";
        }
        Matcher matcher = PATTERN.matcher(value);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            String matcherKey = matcher.group(1);
            String matcherValue = properties.getProperty(matcherKey);
            if (matcherValue != null) {
                matcher.appendReplacement(buffer, matcherValue);
            }
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    public static int getInt(String key) {
        return Integer.parseInt(getString(key));
    }

    public static long getLong(String key) {
        return Long.parseLong(getString(key));
    }

    public static String getTableName(String key) {
        String tableName = getString(key);
        return tableName;
    }

    public static Properties getKafkaProducerProps() {
        return kafkaProducerProps;
    }

    public static Properties getProperties() {
        return properties;
    }
}
