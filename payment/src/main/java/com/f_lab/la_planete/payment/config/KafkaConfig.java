package com.f_lab.la_planete.payment.config;


import com.f_lab.la_planete.core.kafka.config.KafkaConsumerConfig;
import com.f_lab.la_planete.core.kafka.config.KafkaProducerConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.Map;

@Configuration
public class KafkaConfig {

  private final String BOOTSTRAP_SERVERS;
  private final boolean AUTO_COMMIT;
  private final int CONCURRENCY;
  private final String TRUSTED_PACKAGES;
  private final String ACK;
  private final String IDEMPOTENCE;
  private final int TOPIC_PARTITIONS;
  private final String PAYMENT_PROCESSED_EVENT;
  private final String PAYMENT_PROCESS_FAILED_EVENT;


  public KafkaConfig(
      @Value("${spring.kafka.consumer.bootstrap-servers}") String BOOTSTRAP_SERVERS,
      @Value("${spring.kafka.consumer.enable-auto-commit}") boolean AUTO_COMMIT,
      @Value("${kafka.container.concurrency:3}") int CONCURRENCY,
      @Value("${spring.kafka.consumer.properties.spring.json.trusted.packages}") String TRUSTED_PACKAGES,
      @Value("${spring.kafka.producer.ack:all}") String ACK,
      @Value("${spring.kafka.producer.enable.idempotence:true}") String IDEMPOTENCE,
      @Value("${kafka.topic.partitions:3}") int TOPIC_PARTITIONS,
      @Value("${payments.events.topic.name}") String PAYMENT_PROCESSED_EVENT,
      @Value("${payments.events.topic.fail}") String PAYMENT_PROCESS_FAILED_EVENT
  ) {
    this.BOOTSTRAP_SERVERS = BOOTSTRAP_SERVERS;
    this.AUTO_COMMIT = AUTO_COMMIT;
    this.CONCURRENCY = CONCURRENCY;
    this.TRUSTED_PACKAGES = TRUSTED_PACKAGES;
    this.ACK = ACK;
    this.IDEMPOTENCE = IDEMPOTENCE;
    this.TOPIC_PARTITIONS = TOPIC_PARTITIONS;
    this.PAYMENT_PROCESSED_EVENT = PAYMENT_PROCESSED_EVENT;
    this.PAYMENT_PROCESS_FAILED_EVENT = PAYMENT_PROCESS_FAILED_EVENT;
  }

  @Bean
  public KafkaConsumerConfig kafkaConsumerConfig() {
    return new KafkaConsumerConfig();
  }

  @Bean
  public Map<String, Object> consumerConfig() {
    return kafkaConsumerConfig().config(BOOTSTRAP_SERVERS, TRUSTED_PACKAGES, AUTO_COMMIT);
  }

  @Bean
  public ConsumerFactory<String, Object> consumerFactory() {
    return kafkaConsumerConfig().consumerFactory(consumerConfig());
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
    return kafkaConsumerConfig().kafkaListenerContainerFactory(CONCURRENCY, consumerFactory());
  }


  @Bean
  public KafkaProducerConfig kafkaProducerConfig() {
    return new KafkaProducerConfig();
  }

  @Bean
  public Map<String, Object> producerConfig() {
    return kafkaProducerConfig().config(BOOTSTRAP_SERVERS, ACK, IDEMPOTENCE);
  }

  @Bean
  public ProducerFactory<String, Object> producerFactory() {
    return kafkaProducerConfig().producerFactory(producerConfig());
  }

  @Bean
  public KafkaTemplate<String, Object> kafkaTemplate() {
    return kafkaProducerConfig().kafkaTemplate(producerFactory());
  }

  @Bean
  public NewTopic foodsReservationFailEvent() {
    return TopicBuilder
        .name(PAYMENT_PROCESSED_EVENT)
        .partitions(TOPIC_PARTITIONS)
        .build();
  }

  @Bean
  public NewTopic foodsFoodsReservationProcessedEvent() {
    return TopicBuilder
        .name(PAYMENT_PROCESS_FAILED_EVENT)
        .partitions(TOPIC_PARTITIONS)
        .build();
  }
}
