package com.f_lab.joyeuse_planete.orders.config;

import com.f_lab.joyeuse_planete.core.monitor.Monitor;
import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MonitorConfig {

  @Bean
  public TimedAspect timedAspect(MeterRegistry registry) {
    return new Monitor(registry).timedAspect();
  }
}
