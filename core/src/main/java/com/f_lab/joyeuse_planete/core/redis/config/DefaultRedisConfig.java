package com.f_lab.joyeuse_planete.core.redis.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

public abstract class DefaultRedisConfig {

  @Value("${spring.data.redis.host}")
  private String REDIS_HOST;

  @Value("${spring.data.redis.port}")
  private int REDIS_PORT;

  @Value("${spring.data.redis.password:none}")
  private String REDIS_PASSWORD;

  @Profile("!prod")
  @Bean
  public RedisConnectionFactory redisConnectionFactoryDefault() {
    return new LettuceConnectionFactory(REDIS_HOST, REDIS_PORT);
  }

  @Profile("prod")
  @Bean
  public RedisConnectionFactory redisConnectionFactory() {
    RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(REDIS_HOST, REDIS_PORT);
    config.setPassword(REDIS_PASSWORD);

    return new LettuceConnectionFactory(config);
  }
}
