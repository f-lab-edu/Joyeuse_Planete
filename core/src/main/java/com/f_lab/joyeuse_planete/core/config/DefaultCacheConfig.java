package com.f_lab.joyeuse_planete.core.config;

import com.f_lab.joyeuse_planete.core.util.time.TimeConstantsString;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;

public class CacheConfig {

  public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
    return RedisCacheManager.builder(connectionFactory)
        .cacheDefaults()
        .build();
  }

  public RedisCacheConfiguration redisCacheConfiguration() {
    return RedisCacheManager.builder().cacheDefaults()
        .entryTtl(Duration.ofMillis(Long.parseLong(TimeConstantsString.THIRTY_MINUTES)))
        .disableCachingNullValues();
  }
}
