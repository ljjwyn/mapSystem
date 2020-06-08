package com.lijiajie.mapsystem.trajectory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@SpringBootApplication
@EnableAspectJAutoProxy
@EnableAsync
@EnableScheduling
public class TrajectoryApplication {

	public static void main(String[] args) {
		SpringApplication.run(TrajectoryApplication.class, args);
	}

	@Bean
	public JedisPool jedisPool(){
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		jedisPoolConfig.setMaxIdle(8);
		jedisPoolConfig.setMaxWaitMillis(10);
		// 连接耗尽时是否阻塞, false报异常,ture阻塞直到超时, 默认true
		jedisPoolConfig.setBlockWhenExhausted(true);
		// 是否启用pool的jmx管理功能, 默认true
		jedisPoolConfig.setJmxEnabled(true);
		JedisPool jedisPool = new JedisPool(jedisPoolConfig, "127.0.0.1", 6379, 100);
		return jedisPool;

	}

}
