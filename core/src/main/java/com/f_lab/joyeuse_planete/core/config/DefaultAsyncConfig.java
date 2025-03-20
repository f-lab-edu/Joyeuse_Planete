package com.f_lab.joyeuse_planete.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@EnableAsync
@Configuration
public class DefaultAsyncConfig {

  private static final int CORE_POOL_SIZE = 20;
  private static final int MAX_POOL_SIZE = Integer.MAX_VALUE;
  private static final int QUEUE_CAPACITY = 100;
  private static final String NAME_PREFIX = "ASYNC_THREAD_";

  @Bean(name = { "executor", "asyncExecutor" })
  public Executor taskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

    executor.setCorePoolSize(CORE_POOL_SIZE);
    executor.setMaxPoolSize(MAX_POOL_SIZE);
    executor.setQueueCapacity(QUEUE_CAPACITY);
    executor.setThreadNamePrefix(NAME_PREFIX);
    executor.initialize();

    return executor;
  }
}
