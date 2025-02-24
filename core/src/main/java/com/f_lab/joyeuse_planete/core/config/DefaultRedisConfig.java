package com.f_lab.joyeuse_planete.core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

public class RedisConfig {

  @Value("${spring.data.redis.host}")
  private String REDIS_HOST;

  @Value("${spring.data.redis.PORT}")
  private int REDIS_PORT;

  @Value("${spring.data.redis.password:none}")
  private String REDIS_PASSWORD;

  public RedisConnectionFactory redisConnectionFactoryDefault() {
    return new LettuceConnectionFactory(REDIS_HOST, REDIS_PORT);
  }
  public RedisConnectionFactory redisConnectionFactory() {
    RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(REDIS_HOST, REDIS_PORT);
    config.setPassword(REDIS_PASSWORD);

    return new LettuceConnectionFactory(config);
  }
}
