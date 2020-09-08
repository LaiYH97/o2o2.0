package com.imooc.o2o.config.dao;

import java.io.IOException;

import javax.sql.DataSource;

import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

@Configuration
public class SqlSessionFactoryConfiguration {

    @Autowired
    private DataSource dataSource;

    //mybatis_config.xml文件的路径
    private static String mybatisConfigFile;

    @Value("${mybatis.config-location}")
    public void setMybatisConfigFile(String mybatisConfigFile) {
        SqlSessionFactoryConfiguration.mybatisConfigFile = mybatisConfigFile;
    }

    //mybatis mapper文件所在路径
    private static String mapperPath;

    @Value("${mybatis.mapper-locations}")
    public void setMapperPath(String mapperPath) {
        SqlSessionFactoryConfiguration.mapperPath = mapperPath;
    }

    //实体类所在的包
    @Value("${mybatis.type-aliases-package}")
    private String typeAliasPackage;
    /*
   	* 创建SqlSessionFactory实例，设置Configuration，设置mapper映射路径，设置datasource数据源
    */

    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactoryBean createsqlSessionFactoryBean() throws IOException {

        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        //设置mybatis configuration扫描路径
        sqlSessionFactoryBean.setConfigLocation(new ClassPathResource(mybatisConfigFile));
        //设置mapper扫描路径
        PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver
        = new PathMatchingResourcePatternResolver();
        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + mapperPath;
        sqlSessionFactoryBean.setMapperLocations(pathMatchingResourcePatternResolver.getResources(packageSearchPath));
        //设置datasource
        sqlSessionFactoryBean.setDataSource(dataSource);
        //设置typeAlias扫描包
        sqlSessionFactoryBean.setTypeAliasesPackage(typeAliasPackage);

        return sqlSessionFactoryBean;

    }
}
