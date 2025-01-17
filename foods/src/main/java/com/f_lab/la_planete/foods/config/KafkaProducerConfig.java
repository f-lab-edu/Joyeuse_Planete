package com.f_lab.la_planete.foods.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {


  @Value("${spring.kafka.producer.bootstrap-servers:localhost:9092}")
  private String BOOTSTRAP_SERVERS;

  @Value("${spring.kafka.producer.ack:all}")
  private String ACK;

  @Value("${spring.kafka.producer.enable.idempotence:true}")
  private String IDEMPOTENCE;

  @Value("${foods.commands.topic.name}")
  private String foodsReserveCommand;

  @Value("${foods.events.topics.name.reserve}")
  private String foodsProcessedEvents;

  @Value("${foods.events.topics.name.fail}")
  private String foodsReservationFailEvent;

  @Value("${kafka.topic.partitions:3}")
  private int TOPIC_PARTITIONS;


  @Bean
  public Map<String, Object> producerConfig() {
    Map<String, Object> config = new HashMap<>();

    config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
    config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, IDEMPOTENCE);
    config.put(ProducerConfig.ACKS_CONFIG, ACK);

    return config;
  }

  public ProducerFactory<String, Object> producerFactory() {
    return new DefaultKafkaProducerFactory<>(producerConfig());
  }

  @Bean
  public KafkaTemplate<String, Object> kafkaTemplate() {
    return new KafkaTemplate<>(producerFactory());
  }

  @Bean
  public NewTopic foodsProcessFoodsReserveCommand() {
    return TopicBuilder
        .name(foodsReserveCommand)
        .partitions(TOPIC_PARTITIONS)
        .build();
  }

  @Bean
  public NewTopic foodsReservationFailEvent() {
    return TopicBuilder
        .name(foodsReservationFailEvent)
        .partitions(TOPIC_PARTITIONS)
        .build();
  }

  @Bean
  public NewTopic foodsFoodsReservationProcessedEvent() {
    return TopicBuilder
        .name(foodsProcessedEvents)
        .partitions(TOPIC_PARTITIONS)
        .build();
  }
}
