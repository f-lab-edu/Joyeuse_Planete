package com.f_lab.joyeuse_planete.core.kafka.config;

import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.transaction.KafkaTransactionManager;

import java.util.Map;

public abstract class KafkaProducerConfig {

  @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
  protected String BOOTSTRAP_SERVERS;

  @Value("${spring.kafka.producer.ack:all}")
  protected String ACK;

  @Value("${spring.kafka.producer.enable.idempotence:true}")
  protected String IDEMPOTENCE;

  abstract protected Map<String, Object> producerConfig();

  public KafkaTemplate<String, Object> kafkaTemplate() {
    return new KafkaTemplate<>(producerFactory());
  }

  public KafkaTransactionManager<String, Object> kafkaTransactionManager() {
    return new KafkaTransactionManager<>(producerFactory());
  }

  public ProducerFactory<String, Object> producerFactory() {
    return new DefaultKafkaProducerFactory<>(producerConfig());
  }
}

