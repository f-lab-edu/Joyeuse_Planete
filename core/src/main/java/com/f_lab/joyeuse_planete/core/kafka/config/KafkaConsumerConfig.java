package com.f_lab.joyeuse_planete.core.kafka.config;

import com.f_lab.joyeuse_planete.core.exceptions.JoyeusePlaneteApplicationException;
import com.f_lab.joyeuse_planete.core.kafka.exceptions.NonRetryableException;
import com.f_lab.joyeuse_planete.core.kafka.exceptions.RetryableException;
import com.f_lab.joyeuse_planete.core.kafka.util.ExceptionUtil;
import com.f_lab.joyeuse_planete.core.util.log.LogUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.util.backoff.BackOff;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;

@Slf4j
public abstract class KafkaConsumerConfig {

  @Value("${spring.kafka.bootstrap-servers}")
  protected String BOOTSTRAP_SERVERS;

  @Value("${spring.kafka.consumer.enable-auto-commit:false}")
  protected boolean AUTO_COMMIT;

  @Value("${kafka.container.concurrency:3}")
  protected int CONCURRENCY;

  @Value("${spring.kafka.consumer.properties.spring.json.trusted.packages:com.f_lab.joyeuse_planete.core.*}")
  protected String TRUSTED_PACKAGES;

  @Value("${spring.kafka.consumer.isolation-level:read_committed}")
  protected String ISOLATION_LEVEL;

  @Value("${retry.attempts:3}")
  private int RETRY_ATTEMPTS;

  public static final String KAFKA_DEAD_LETTER_TOPIC_ID = "kafka_dead_letter_topic-id";

  abstract public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory();

  abstract protected Map<String, Object> consumerConfig();

  abstract protected String deadLetterTopicName();

  protected BiFunction<ConsumerRecord<?, ?>, Exception, TopicPartition> deadLetterTopicStrategy() {
    return defaultDeadLetterTopicStrategy(deadLetterTopicName());
  }

  protected BiFunction<ConsumerRecord<?, ?>, Exception, TopicPartition> defaultDeadLetterTopicStrategy(String deadLetterTopic) {
    return (record, ex) -> {
      ex = ExceptionUtil.unwrap(ex);

      LogUtil.exception("KafkaConsumerConfig.defaultDeadLetterTopicStrategy", ex);

      record.headers().add(KAFKA_DEAD_LETTER_TOPIC_ID, UUID.randomUUID().toString().getBytes());
      record.headers().add(KafkaHeaders.EXCEPTION_FQCN, ex.getClass().getName().getBytes());
      record.headers().add(KafkaHeaders.EXCEPTION_MESSAGE, ((ex.getMessage() != null) ? ex.getMessage() : "null").getBytes());
      record.headers().add(KafkaHeaders.ORIGINAL_TOPIC, record.topic().getBytes());
      record.headers().add(KafkaHeaders.DLT_EXCEPTION_STACKTRACE, Arrays.toString(ex.getStackTrace()).getBytes());

      return new TopicPartition(deadLetterTopic, -1);
    };
  }

  protected DeadLetterPublishingRecoverer deadLetterPublishingRecoverer() {
    return null;
  }

  public BackOff defaultBackOffStrategy() {
    return new ExponentialBackOffWithMaxRetries(RETRY_ATTEMPTS);
  }

  public DefaultErrorHandler defaultErrorHandler() {
    DefaultErrorHandler errorHandler = new DefaultErrorHandler(
        deadLetterPublishingRecoverer(),
        defaultBackOffStrategy());

    errorHandler.addNotRetryableExceptions(NonRetryableException.class, JoyeusePlaneteApplicationException.class);
    errorHandler.addRetryableExceptions(RetryableException.class);

    return errorHandler;
  }

  public ConsumerFactory<String, Object> consumerFactory() {
    return new DefaultKafkaConsumerFactory<>(consumerConfig());
  }
}
