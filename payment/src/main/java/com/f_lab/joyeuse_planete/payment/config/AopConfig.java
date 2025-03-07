package com.f_lab.joyeuse_planete.payment.config;

import com.f_lab.joyeuse_planete.core.aspect.RetryAspect;
import com.f_lab.joyeuse_planete.core.kafka.aspect.KafkaDeadLetterTopicAspect;
import com.f_lab.joyeuse_planete.core.kafka.repository.DeadLetterTopicRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
public class AopConfig {

  @Bean
  public RetryAspect lockRetryAspect() {
    return new RetryAspect();
  }

  @Bean
  public KafkaDeadLetterTopicAspect kafkaUpdateStatusAspect(
      KafkaDeadLetterTopicAspect.DeadLetterTopicService deadLetterTopicService) {

    return new KafkaDeadLetterTopicAspect(deadLetterTopicService);
  }

  @Bean
  public KafkaDeadLetterTopicAspect.DeadLetterTopicService deadLetterTopicService(DeadLetterTopicRepository deadLetterTopicRepository) {
    return new KafkaDeadLetterTopicAspect.DeadLetterTopicService(deadLetterTopicRepository);
  }
}
