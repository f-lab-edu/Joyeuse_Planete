package com.f_lab.la_planete.core.kafka.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;


public class KafkaProducerConfig {

  public Map<String, Object> config(
      String BOOTSTRAP_SERVERS,
      String ACK,
      String IDEMPOTENCE
  ) {
    Map<String, Object> config = new HashMap<>();

    config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
    config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, IDEMPOTENCE);
    config.put(ProducerConfig.ACKS_CONFIG, ACK);

    return config;
  }

  public ProducerFactory<String, Object> producerFactory(Map<String, Object> config) {
    return new DefaultKafkaProducerFactory<>(config);
  }

  public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
    return new KafkaTemplate<>(producerFactory);
  }
}
