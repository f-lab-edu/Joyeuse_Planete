package com.f_lab.joyeuse_planete.core.kafka.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;


public class KafkaConsumerConfig {

  public Map<String, Object> config(String BOOTSTRAP_SERVERS, String TRUSTED_PACKAGES, boolean AUTO_COMMIT
  ) {
    Map<String, Object> config = new HashMap<>();

    config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
    config.put(JsonDeserializer.TRUSTED_PACKAGES, TRUSTED_PACKAGES);
    config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, AUTO_COMMIT);
    config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

    return config;
  }

  public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
      int CONCURRENCY,
      ConsumerFactory<String, Object> consumerFactory
  ) {
    ConcurrentKafkaListenerContainerFactory<String, Object> factory =
        new ConcurrentKafkaListenerContainerFactory<>();

    factory.setConsumerFactory(consumerFactory);
    factory.setConcurrency(CONCURRENCY);

    return factory;
  }

  public ConsumerFactory<String, Object> consumerFactory(Map<String, Object> config) {
    return new DefaultKafkaConsumerFactory<>(config);
  }
}
