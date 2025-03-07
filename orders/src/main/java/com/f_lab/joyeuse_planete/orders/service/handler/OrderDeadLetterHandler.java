package com.f_lab.joyeuse_planete.orders.service.handler;

import com.f_lab.joyeuse_planete.core.events.FoodReleaseFailedEvent;
import com.f_lab.joyeuse_planete.core.events.FoodReservationFailedEvent;
import com.f_lab.joyeuse_planete.core.events.FoodReservationProcessedEvent;
import com.f_lab.joyeuse_planete.core.kafka.annotation.KafkaDeadLetterTopic;
import com.f_lab.joyeuse_planete.core.kafka.domain.DeadLetterTopic;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import static com.f_lab.joyeuse_planete.core.kafka.config.KafkaConsumerConfig.KAFKA_DEAD_LETTER_TOPIC_ID;


@Component
@RequiredArgsConstructor
@KafkaListener(topics = { "${orders.dead-letter-topic}" }, groupId = "${spring.kafka.consumer.group-id}")
public class OrderDeadLetterHandler {

  private final KafkaDeadLetterTopicHandler kafkaDeadLetterTopicHandler;


  @KafkaHandler
  public void processDeadEvent(@Payload FoodReservationProcessedEvent foodReservationProcessedEvent,
                               @Header(value = KAFKA_DEAD_LETTER_TOPIC_ID, required = false) String topicId,
                               @Header(value = KafkaHeaders.EXCEPTION_FQCN, required = false) String exceptionName,
                               @Header(value = KafkaHeaders.EXCEPTION_MESSAGE, required = false) String exceptionMessage,
                               @Header(value = KafkaHeaders.DLT_EXCEPTION_STACKTRACE, required = false) String exceptionStackTrace,
                               @Header(value = KafkaHeaders.ORIGINAL_TOPIC, required = false) String originalTopic) {

    DeadLetterTopic deadLetter = DeadLetterTopic.createInstance(
        foodReservationProcessedEvent,
        foodReservationProcessedEvent.getClass().getSimpleName(),
        topicId,
        exceptionName,
        exceptionMessage,
        exceptionStackTrace,
        originalTopic
    );

    kafkaDeadLetterTopicHandler.handleDeadEventsForRetries(deadLetter);
  }

  @KafkaHandler
  public void processDeadEvent(@Payload FoodReservationFailedEvent foodReservationFailedEvent,
                               @Header(value = KAFKA_DEAD_LETTER_TOPIC_ID, required = false) String topicId,
                               @Header(value = KafkaHeaders.EXCEPTION_FQCN, required = false) String exceptionName,
                               @Header(value = KafkaHeaders.EXCEPTION_MESSAGE, required = false) String exceptionMessage,
                               @Header(value = KafkaHeaders.DLT_EXCEPTION_STACKTRACE, required = false) String exceptionStackTrace,
                               @Header(value = KafkaHeaders.ORIGINAL_TOPIC, required = false) String originalTopic) {

    DeadLetterTopic deadLetter = DeadLetterTopic.createInstance(
        foodReservationFailedEvent,
        foodReservationFailedEvent.getClass().getSimpleName(),
        topicId,
        exceptionName,
        exceptionMessage,
        exceptionStackTrace,
        originalTopic
    );

    kafkaDeadLetterTopicHandler.handleDeadEventsForRetries(deadLetter);
  }

  @KafkaHandler
  public void processDeadEvent(@Payload FoodReleaseFailedEvent foodReleaseFailedEvent,
                               @Header(value = KAFKA_DEAD_LETTER_TOPIC_ID, required = false) String topicId,
                               @Header(value = KafkaHeaders.EXCEPTION_FQCN, required = false) String exceptionName,
                               @Header(value = KafkaHeaders.EXCEPTION_MESSAGE, required = false) String exceptionMessage,
                               @Header(value = KafkaHeaders.DLT_EXCEPTION_STACKTRACE, required = false) String exceptionStackTrace,
                               @Header(value = KafkaHeaders.ORIGINAL_TOPIC, required = false) String originalTopic) {

    DeadLetterTopic deadLetter = DeadLetterTopic.createInstance(
        foodReleaseFailedEvent,
        foodReleaseFailedEvent.getClass().getSimpleName(),
        topicId,
        exceptionName,
        exceptionMessage,
        exceptionStackTrace,
        originalTopic
    );

    kafkaDeadLetterTopicHandler.handleDeadEventsForRetries(deadLetter);
  }

  @Component
  @RequiredArgsConstructor
  static class KafkaDeadLetterTopicHandler {

    @KafkaDeadLetterTopic
    public void handleDeadEventsForRetries(DeadLetterTopic deadLetterTopic) {
    }
  }
}
