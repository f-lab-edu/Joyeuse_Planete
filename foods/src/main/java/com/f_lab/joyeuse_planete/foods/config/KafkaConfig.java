package com.f_lab.joyeuse_planete.foods.config;




import com.f_lab.joyeuse_planete.core.kafka.config.KafkaConsumerConfig;
import com.f_lab.joyeuse_planete.core.kafka.config.KafkaProducerConfig;
import com.f_lab.joyeuse_planete.core.kafka.exceptions.NonRetryableException;
import com.f_lab.joyeuse_planete.core.kafka.exceptions.RetryableException;
import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.context.annotation.Primary;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.transaction.KafkaTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.util.backoff.FixedBackOff;

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
  private final String TRANSACTION_ID;
  private final String ISOLATION_LEVEL;
  private final int TOPIC_PARTITIONS;
  private final String FOOD_RESERVATION_FAILED_EVENT;

  public KafkaConfig(
      @Value("${spring.kafka.bootstrap-servers}") String BOOTSTRAP_SERVERS,
      @Value("${spring.kafka.consumer.enable-auto-commit}") boolean AUTO_COMMIT,
      @Value("${kafka.container.concurrency:3}") int CONCURRENCY,
      @Value("${spring.kafka.consumer.properties.spring.json.trusted.packages}") String TRUSTED_PACKAGES,
      @Value("${spring.kafka.producer.ack:all}") String ACK,
      @Value("${spring.kafka.producer.enable.idempotence:true}") String IDEMPOTENCE,
      @Value("${kafka.topic.partitions:3}") int TOPIC_PARTITIONS,
      @Value("${foods.events.topic.fail}") String FOOD_RESERVATION_FAILED_EVENT,
      @Value("${spring.kafka.producer.transaction-id-prefix}") String TRANSACTION_ID,
      @Value("${spring.kafka.consumer.isolation-level}") String ISOLATION_LEVEL
  ) {
    this.BOOTSTRAP_SERVERS = BOOTSTRAP_SERVERS;
    this.AUTO_COMMIT = AUTO_COMMIT;
    this.CONCURRENCY = CONCURRENCY;
    this.TRUSTED_PACKAGES = TRUSTED_PACKAGES;
    this.ACK = ACK;
    this.IDEMPOTENCE = IDEMPOTENCE;
    this.TOPIC_PARTITIONS = TOPIC_PARTITIONS;
    this.FOOD_RESERVATION_FAILED_EVENT = FOOD_RESERVATION_FAILED_EVENT;
    this.TRANSACTION_ID = TRANSACTION_ID;
    this.ISOLATION_LEVEL = ISOLATION_LEVEL;
  }

  @Bean
  public KafkaConsumerConfig kafkaConsumerConfig() {
    return new KafkaConsumerConfig();
  }

  @Bean
  public Map<String, Object> consumerConfig() {
    Map<String, Object> config = kafkaConsumerConfig().config(BOOTSTRAP_SERVERS, TRUSTED_PACKAGES, AUTO_COMMIT);
    config.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, ISOLATION_LEVEL);
    return config;
  }

  @Bean
  public ConsumerFactory<String, Object> consumerFactory() {
    return kafkaConsumerConfig().consumerFactory(consumerConfig());
  }

  // KAFKA RETRY LOGIC
  @Bean
  public DefaultErrorHandler defaultErrorHandler() {
    DefaultErrorHandler errorHandler = new DefaultErrorHandler(
        new DeadLetterPublishingRecoverer(kafkaTemplate()),
        new FixedBackOff(1000L, 3));

    errorHandler.addNotRetryableExceptions(NonRetryableException.class);
    errorHandler.addRetryableExceptions(RetryableException.class);

    return errorHandler;
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, Object> factory = kafkaConsumerConfig().kafkaListenerContainerFactory(CONCURRENCY, consumerFactory());
    factory.setCommonErrorHandler(defaultErrorHandler());
    return factory;
  }

  @Bean
  public KafkaProducerConfig kafkaProducerConfig() {
    return new KafkaProducerConfig();
  }

  @Bean
  public Map<String, Object> producerConfig() {
    Map<String, Object> config = kafkaProducerConfig().config(BOOTSTRAP_SERVERS, ACK, IDEMPOTENCE);
    config.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, TRANSACTION_ID);
    return config;
  }

  @Bean
  public ProducerFactory<String, Object> producerFactory() {
    return kafkaProducerConfig().producerFactory(producerConfig());
  }

  @Bean
  public KafkaTemplate<String, Object> kafkaTemplate() {
    return kafkaProducerConfig().kafkaTemplate(producerFactory());
  }

  @Primary
  @Bean(name = { "jpaTransactionManager", "transactionManager" })
  public JpaTransactionManager jpaTransactionManager(EntityManagerFactory emf) {
    return new JpaTransactionManager(emf);
  }

  @Bean("kafkaTransactionManager")
  public KafkaTransactionManager<String, Object> kafkaTransactionManager() {
    return new KafkaTransactionManager<>(producerFactory());
  }

  @Bean
  public NewTopic foodsFoodsReservationProcessedEvent() {
    return TopicBuilder
        .name(FOOD_RESERVATION_FAILED_EVENT)
        .partitions(TOPIC_PARTITIONS)
        .build();
  }
}
