package com.f_lab.joyeuse_planete.payment.config;


import com.f_lab.joyeuse_planete.core.kafka.config.KafkaConsumerConfig;
import com.f_lab.joyeuse_planete.core.kafka.config.KafkaProducerConfig;
import com.f_lab.joyeuse_planete.core.kafka.service.KafkaService;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.transaction.KafkaTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class KafkaConfig {

  @Value("${kafka.topic.partitions:3}")
  private int TOPIC_PARTITIONS;

  @Value("${payments.events.topic.name}")
  private String PAYMENT_PROCESSED_EVENT;

  @Value("${payments.events.topic.fail}")
  private String PAYMENT_PROCESS_FAILED_EVENT;

  @Bean
  public KafkaService kafkaService(KafkaTemplate<String, Object> kafkaTemplate) {
    return new KafkaService(kafkaTemplate);
  }

  @Bean
  public NewTopic paymentProcessedEvent() {
    return TopicBuilder
        .name(PAYMENT_PROCESSED_EVENT)
        .partitions(TOPIC_PARTITIONS)
        .build();
  }

  @Bean
  public NewTopic paymentProcessingFailEvent() {
    return TopicBuilder
        .name(PAYMENT_PROCESS_FAILED_EVENT)
        .partitions(TOPIC_PARTITIONS)
        .build();
  }

  @Primary
  @Bean(name = { "transactionManager", "jpaTransactionManager" })
  public JpaTransactionManager jpaTransactionManager(EntityManagerFactory emf) {
    return new JpaTransactionManager(emf);
  }

  // KAFKA PRODUCER
  @Configuration
  static class CustomKafkaProducerConfig extends KafkaProducerConfig {

    @Value("${spring.kafka.producer.transaction-id-prefix}")
    private String TRANSACTION_ID;

    @Override
    public Map<String, Object> producerConfig() {
      Map<String, Object> config = new HashMap<>();

      config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
      config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
      config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
      config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, IDEMPOTENCE);
      config.put(ProducerConfig.ACKS_CONFIG, ACK);
      config.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, TRANSACTION_ID);

      return config;
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
      return super.kafkaTemplate();
    }

    @Bean("kafkaTransactionManager")
    public KafkaTransactionManager<String, Object> kafkaTransactionManager() {
      return super.kafkaTransactionManager();
    }
  }

  // KAFKA CONSUMER
  @Configuration
  @RequiredArgsConstructor
  static class CustomKafkaConsumerConfig extends KafkaConsumerConfig {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${payments.dead-letter-topic.name}")
    private String DEAD_LETTER_TOPIC;

    @Override
    protected Map<String, Object> consumerConfig() {
      Map<String, Object> config = new HashMap<>();

      config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
      config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
      config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
      config.put(JsonDeserializer.TRUSTED_PACKAGES, TRUSTED_PACKAGES);
      config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, AUTO_COMMIT);
      config.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, ISOLATION_LEVEL);

      return config;
    }

    @Override
    protected String deadLetterTopicName() {
      return DEAD_LETTER_TOPIC;
    }

    @Override
    protected DeadLetterPublishingRecoverer deadLetterPublishingRecoverer() {
      return new DeadLetterPublishingRecoverer(kafkaTemplate, deadLetterTopicStrategy());
    }

    @Override
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
      ConcurrentKafkaListenerContainerFactory<String, Object> factory =
          new ConcurrentKafkaListenerContainerFactory<>();

      factory.setConsumerFactory(consumerFactory());
      factory.setConcurrency(CONCURRENCY);
      factory.setCommonErrorHandler(defaultErrorHandler());

      return factory;
    }
  }
}
