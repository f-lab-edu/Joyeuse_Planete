package com.f_lab.joyeuse_planete.core.kafka.config;

import com.f_lab.joyeuse_planete.core.kafka.exceptions.NonRetryableException;
import com.f_lab.joyeuse_planete.core.kafka.exceptions.RetryableException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

import java.util.Map;

public abstract class KafkaConsumerConfig {

  @Value("${spring.kafka.bootstrap-servers}")
  protected String BOOTSTRAP_SERVERS;

  @Value("${spring.kafka.consumer.enable-auto-commit}")
  protected boolean AUTO_COMMIT;

  @Value("${kafka.container.concurrency}")
  protected int CONCURRENCY;

  @Value("${spring.kafka.consumer.properties.spring.json.trusted.packages}")
  protected String TRUSTED_PACKAGES;

  @Value("${spring.kafka.consumer.isolation-level}")
  protected String ISOLATION_LEVEL;


  abstract public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory();

  abstract protected Map<String, Object> consumerConfig();

  protected DeadLetterPublishingRecoverer deadLetterPublishingRecoverer() {
    return null;
  }

  public FixedBackOff defaultBackOffStrategy() {
    return new FixedBackOff(1500L, 10);
  }

  public DefaultErrorHandler defaultErrorHandler() {
    DefaultErrorHandler errorHandler = new DefaultErrorHandler(
        deadLetterPublishingRecoverer(),
        defaultBackOffStrategy());

    errorHandler.addNotRetryableExceptions(NonRetryableException.class);
    errorHandler.addRetryableExceptions(RetryableException.class);

    return errorHandler;
  }

  public ConsumerFactory<String, Object> consumerFactory() {
    return new DefaultKafkaConsumerFactory<>(consumerConfig());
  }
}
