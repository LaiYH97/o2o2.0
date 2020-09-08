package com.imooc.o2o.config.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.imooc.o2o.cache.JedisPoolWriper;
import com.imooc.o2o.cache.JedisUtil;

import redis.clients.jedis.JedisPoolConfig;

/**
 * @author Administrator
 * redis的配置
 */
@Configuration
public class RedisConfiguration {

	@Value("${spring.redis.host}")
    private String hostname;
	
    @Value("${spring.redis.port}")
    private int port;
    
    @Value("${spring.redis.jedis.pool.max-active}")
    private int maxTotal;

    @Value("${spring.redis.jedis.pool.max-idle}")
    private int maxIdle;

    @Value("${spring.redis.jedis.pool.max-wait}")
    private long maxWaitMillis;

    @Value("${redis.pool.testOnBorrow}")
    private boolean testOnBorrow;

    @Autowired
    private JedisPoolConfig jedisPoolConfig;

    @Autowired
    private JedisPoolWriper jedisPoolWriper;

    @Autowired
    private JedisUtil jedisUtil;

    /**
             * 创建redis连接池的设置
     * @return
     */
    @Bean(name = "jedisPoolConfig")
    public JedisPoolConfig createJedisPoolConfig(){

        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        //控制一个pool可以控制多少jedis实例
        jedisPoolConfig.setMaxIdle(maxIdle);
        //连接池中最多可空闲maxIdle个连接，这里取值为20，表示即使没有数据库连接时依然可以保持20空闲连接
        jedisPoolConfig.setMaxTotal(maxTotal);
        //最大等待时间
        jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);
        //在获取连接时检查有效性
        jedisPoolConfig.setTestOnBorrow(testOnBorrow);

        return jedisPoolConfig;
    }

    /**
             *创建Redis连接池并做相关配置 
     * @return
     */
    @Bean(name = "jedisPoolWriper")
    public JedisPoolWriper jedisPoolWriper(){

        return new JedisPoolWriper(jedisPoolConfig,hostname,port);

    }

    /**
	  *创建Redis工具类，封装好Redis的连接以进行相关的操作
	* @return
	*/
    @Bean("jedisUtil")
    public JedisUtil jedisUtil(){
        JedisUtil jedisUtil = new JedisUtil();
        jedisUtil.setJedisPool(jedisPoolWriper);
        return jedisUtil;
    }

    /**
     * Redis的Key操作
     * @return
     */
    @Bean(name = "jedisKeys")
    public JedisUtil.Keys createJedisKeys(){
    	JedisUtil.Keys jedisKeys = jedisUtil.new Keys();
        return jedisKeys;
    }
    
    /**
     * Redis的String操作
     * @return
     */
    @Bean(name = "jedisStrings")
    public JedisUtil.Strings createJedisStrings(){
    	JedisUtil.Strings jedisStrings = jedisUtil.new Strings();
        return jedisStrings;
    }

}
