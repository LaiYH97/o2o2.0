package com.imooc.o2o.config.service;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

@Configuration
//开启事务支持
@EnableTransactionManagement
public class TransactionManagementConfiguration implements TransactionManagementConfigurer {

    /**
          * 注入datasource
     */
    @Autowired
    private DataSource dataSource;

    /**
           *关于事务管理，需要返回PlatformTransactionManager的实现
     */
    @Override
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return new DataSourceTransactionManager(dataSource);
    }
}
