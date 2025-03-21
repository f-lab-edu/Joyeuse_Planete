package com.f_lab.joyeuse_planete.foods.config;

import com.f_lab.joyeuse_planete.core.config.DefaultAsyncConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AsyncConfig {

  @Bean
  public DefaultAsyncConfig defaultAsyncConfig() {
    return new DefaultAsyncConfig();
  }
}
