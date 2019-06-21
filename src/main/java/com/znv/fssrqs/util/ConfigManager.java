package com.znv.fssrqs.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ct on 2016-12-14.
 */
@Slf4j
public class ConfigManager {
    private static final Logger L = LoggerFactory.getLogger(ConfigManager.class);
    private static final Pattern PATTERN = Pattern.compile("\\$\\{([^\\}]+)\\}"); // 正则匹配
    private static Properties props = new Properties();
    private static Properties producerProps = new Properties();
    private static Properties blackAlarmProps = new Properties();
    private static Properties consumerProps = new Properties();
    private static String groupId = null;

    private ConfigManager() {
    }

    // 读取HDFS上的配置文件
    public static void init(String fileHdfsPath) {

        Configuration conf = new Configuration();
        FSDataInputStream in = null;
        String configPath = fileHdfsPath + "/config/fss.properties";
        String hdfsFilPath = fileHdfsPath + "/config";
        try {
            FileSystem fs = FileSystem.get(URI.create(configPath), conf);
            in = fs.open(new Path(configPath));
            props.load(in);
            props.setProperty("hdfs_url", fileHdfsPath);
            props.setProperty("hdfsFilePath", hdfsFilPath);

        } catch (IOException e) {
            L.error("read HDFS config error {}", e);
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    L.error("close HDFS error {}", e);
                }
            }
        }
    }

    /**
     * @param key 获取Properties对象中key对应value, 并替换value中引用变量的部分为实际的值
     * @return
     */
    public static String getString(String key) {
        String value = props.getProperty(key);
        if (StringUtils.isEmpty(value)) {
            log.warn("Can't,find property name={}", key);
            return "";
        }
        Matcher matcher = PATTERN.matcher(value);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            String matcherKey = matcher.group(1);
            String matchervalue = props.getProperty(matcherKey);
            if (matchervalue != null) {
                matcher.appendReplacement(buffer, matchervalue);
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

    /**
     * @param fileHdfsPath
     */
    public static void producerInit(String fileHdfsPath) {
        String producerPath = fileHdfsPath + "/config/producerBasic.properties";
        Configuration conf = new Configuration();
        FSDataInputStream in = null;
        try {
            FileSystem fs = FileSystem.get(URI.create(producerPath), conf);
            in = fs.open(new Path(producerPath));
            producerProps.load(in);
            producerProps.setProperty("value.serializer","com.znv.fssrqs.kafka.common.KafkaAvroSerializer");
        } catch (IOException e) {
            L.error("read producerBasic.properties config error {}", e);
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    L.error("close  producerBasic.properties error {}", e);
                }
            }
        }
    }

    /**
     * @return 获取生产者配置信息
     */
    public static Properties getProducerProps() {
        return producerProps;
    }
}
