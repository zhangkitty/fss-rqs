package com.znv.fssrqs.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = "com.znv.fssrqs.dao.hbase",sqlSessionFactoryRef = "hbaseSqlSessionFactory")
public class HbaseDataSourceConfig {

    @Bean(name = "hbaseDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.hbase")
    public DataSource testDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "hbaseSqlSessionFactory")
    public SqlSessionFactory hbaseSqlSessionFactory(@Qualifier("hbaseDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mybatis/mapper/hbase/*.xml"));
        bean.setTypeAliasesPackage("com.znv.fssrqs.entity.hbase");
        return bean.getObject();
    }

    @Bean(name = "hbaseSqlSessionTemplate")
    public SqlSessionTemplate hbaseSqlSessionTemplate(@Qualifier("hbaseSqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        SqlSessionTemplate template = new SqlSessionTemplate(sqlSessionFactory); // 使用上面配置的Factory
        return template;
    }





}
