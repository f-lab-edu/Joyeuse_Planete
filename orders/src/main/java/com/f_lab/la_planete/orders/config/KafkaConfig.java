package com.f_lab.la_planete.orders.config;



import com.f_lab.la_planete.core.kafka.config.KafkaConsumerConfig;
import com.f_lab.la_planete.core.kafka.config.KafkaProducerConfig;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Configuration
public class KafkaConfig {

  private final String BOOTSTRAP_SERVERS;
  private final boolean AUTO_COMMIT;
  private final int CONCURRENCY;
  private final String TRUSTED_PACKAGES;
  private final String ACK;
  private final String IDEMPOTENCE;
  private final int TOPIC_PARTITIONS;
  private final String ORDER_CREATED_EVENT;
  private final String ORDER_CREATION_FAILED_EVENT;


  public KafkaConfig(
      @Value("${spring.kafka.bootstrap-servers}") String BOOTSTRAP_SERVERS,
      @Value("${spring.kafka.consumer.enable-auto-commit}") boolean AUTO_COMMIT,
      @Value("${kafka.container.concurrency:3}") int CONCURRENCY,
      @Value("${spring.kafka.consumer.properties.spring.json.trusted.packages}") String TRUSTED_PACKAGES,
      @Value("${spring.kafka.producer.ack:all}") String ACK,
      @Value("${spring.kafka.producer.enable.idempotence:true}") String IDEMPOTENCE,
      @Value("${kafka.topic.partitions:3}") int TOPIC_PARTITIONS,
      @Value("${orders.events.topic.name}") String ORDER_CREATED_EVENT,
      @Value("${orders.events.topic.fail}") String ORDER_CREATION_FAILED_EVENT
  ) {
    this.BOOTSTRAP_SERVERS = BOOTSTRAP_SERVERS;
    this.AUTO_COMMIT = AUTO_COMMIT;
    this.CONCURRENCY = CONCURRENCY;
    this.TRUSTED_PACKAGES = TRUSTED_PACKAGES;
    this.ACK = ACK;
    this.IDEMPOTENCE = IDEMPOTENCE;
    this.TOPIC_PARTITIONS = TOPIC_PARTITIONS;
    this.ORDER_CREATED_EVENT = ORDER_CREATED_EVENT;
    this.ORDER_CREATION_FAILED_EVENT = ORDER_CREATION_FAILED_EVENT;
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
  public NewTopic orderCreatedEvent() {
    return TopicBuilder
        .name(ORDER_CREATED_EVENT)
        .partitions(TOPIC_PARTITIONS)
        .build();
  }

  @Bean
  public NewTopic orderCreationFailedEvent() {
    return TopicBuilder
        .name(ORDER_CREATION_FAILED_EVENT)
        .partitions(TOPIC_PARTITIONS)
        .build();
  }
}
