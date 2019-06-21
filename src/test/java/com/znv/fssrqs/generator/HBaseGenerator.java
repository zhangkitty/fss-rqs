//package com.znv.fssrqs.generator;
//
//import org.mybatis.generator.api.MyBatisGenerator;
//import org.mybatis.generator.config.Configuration;
//import org.mybatis.generator.config.xml.ConfigurationParser;
//import org.mybatis.generator.exception.InvalidConfigurationException;
//import org.mybatis.generator.exception.XMLParserException;
//import org.mybatis.generator.internal.DefaultShellCallback;
//
//import java.io.File;
//import java.io.IOException;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by dongzelong on  2019/6/19 15:05.
// *
// * @author dongzelong
// * @version 1.0
// * @Description TODO
// */
//public class HBaseGenerator {
//    public static void main(String[] args) throws IOException, XMLParserException, InvalidConfigurationException, SQLException, InterruptedException {
//        List<String> warnings = new ArrayList<String>();
//
//        boolean overwrite = true;
//        // 指定代码生成配置文件的位置
//        File configFile = new File("D:\\project\\face\\gitlab-face\\git-fnms-new\\fnms-server\\src\\test\\resources\\hbase-generatorConfig.xml");
//        ConfigurationParser cp = new ConfigurationParser(warnings);
//        Configuration config = cp.parseConfiguration(configFile);
//        DefaultShellCallback callback = new DefaultShellCallback(overwrite);
//        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
//        myBatisGenerator.generate(null);
//    }
//}
