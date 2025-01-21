package com.f_lab.la_planete.foods.config;

import com.f_lab.la_planete.core.aspect.LockRetryAspect;
import com.f_lab.la_planete.core.kafka.aspect.KafkaRetryAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
public class AopConfig {
  @Bean
  public LockRetryAspect lockRetryAspect() {
    return new LockRetryAspect();
  }

  @Bean
  public KafkaRetryAspect kafkaRetryAspect() {
    return new KafkaRetryAspect();
  }
}
