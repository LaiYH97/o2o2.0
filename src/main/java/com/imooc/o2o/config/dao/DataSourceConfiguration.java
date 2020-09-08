package com.imooc.o2o.config.dao;

import java.beans.PropertyVetoException;

import javax.sql.DataSource;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.imooc.o2o.util.DESUtils;
import com.mchange.v2.c3p0.ComboPooledDataSource;

/*
 * 数据库相关配置
 * 配置datasource到ioc容器里面
 */

@Configuration
//配置mapper的扫描路径
@MapperScan(basePackages = "com.imooc.o2o.dao")
public class DataSourceConfiguration {

    @Value("${spring.datasource.driver-class-name}")
    private String jdbcDriver;

    @Value("${spring.datasource.url}")
    private String jdbcUrl;

    @Value("${spring.datasource.username}")
    private String jdbcUsername;

    @Value("${spring.datasource.password}")
    private String jdbcPassword;


    /**
             * 数据源 dataSource
     * @see ComboPooledDataSource
     */
    @Bean(name = "dataSource")
    public DataSource dataSource() throws PropertyVetoException {

        //生成datasoutce
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        //驱动
        dataSource.setDriverClass(jdbcDriver);

        dataSource.setJdbcUrl(jdbcUrl);

        dataSource.setUser(DESUtils.getDecryptString(jdbcUsername));

        dataSource.setPassword(DESUtils.getDecryptString(jdbcPassword));

        //设置连接池最大线程数
        dataSource.setMaxPoolSize(30);

        //设置最小线程数
        dataSource.setMinPoolSize(10);

        //关闭连接后不自动commit
        dataSource.setAutoCommitOnClose(false);

        //连接超时时间
        dataSource.setCheckoutTimeout(10000);

        //连接失败重试次数
        dataSource.setAcquireRetryAttempts(2);

        return dataSource;
    }

}
