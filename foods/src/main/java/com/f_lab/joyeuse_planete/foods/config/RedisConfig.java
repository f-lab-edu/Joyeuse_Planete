package com.f_lab.joyeuse_planete.foods.config;


import com.f_lab.joyeuse_planete.core.redis.config.DefaultCacheConfig;
import com.f_lab.joyeuse_planete.core.redis.config.DefaultRedisConfig;
import com.f_lab.joyeuse_planete.foods.dto.request.FoodSearchCondition;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;


import java.time.Duration;

import static com.f_lab.joyeuse_planete.core.util.time.TimeConstants.TimeConstantsString.THIRTY_MINUTES;


@Configuration
@EnableCaching
public class RedisConfig {

  @Configuration
  static class FoodRedisConfig extends DefaultRedisConfig {
  }

  @Configuration
  static class FoodCacheConfig extends DefaultCacheConfig {

    protected RedisCacheConfiguration redisCacheConfiguration() {
      return RedisCacheManager.builder().cacheDefaults()
          .entryTtl(Duration.ofMillis(Long.parseLong(THIRTY_MINUTES)))
          .disableCachingNullValues();
    }

    @Bean("foodSearchKeyGenerator")
    public KeyGenerator foodSearchKeyGenerator() {
      return (target, method, params) -> {
        FoodSearchCondition condition = (FoodSearchCondition) params[0];

        return String.format(
            "lat:%f-lon:%f-search:%s-page:%d-size:%d-sortBy:%s",
            condition.getLat(),
            condition.getLon(),
            condition.getSearch() != null ? condition.getSearch() : "NONE",
            condition.getPage(),
            condition.getSize(),
            String.join(",", condition.getSortBy())
        );
      };
    }
  }
}
