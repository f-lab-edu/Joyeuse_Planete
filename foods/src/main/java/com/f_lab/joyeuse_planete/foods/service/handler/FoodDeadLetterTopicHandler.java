package com.f_lab.joyeuse_planete.foods.service.handler;

import com.f_lab.joyeuse_planete.core.events.FoodReleaseEvent;
import com.f_lab.joyeuse_planete.core.events.FoodReleaseFailedEvent;
import com.f_lab.joyeuse_planete.core.events.FoodReservationFailedEvent;
import com.f_lab.joyeuse_planete.core.events.FoodReservationProcessedEvent;
import com.f_lab.joyeuse_planete.core.kafka.exceptions.RetryableException;
import com.f_lab.joyeuse_planete.core.kafka.service.KafkaService;
import com.f_lab.joyeuse_planete.core.kafka.util.ExceptionUtil;
import com.f_lab.joyeuse_planete.core.util.log.LogUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.f_lab.joyeuse_planete.core.util.time.TimeConstants.TimeConstantsString.FIVE_SECONDS;


@Slf4j
@Component
@RequiredArgsConstructor
@KafkaListener(topics = "${foods.dead-letter-topic}", groupId = "${spring.kafka.consumer.group-id}")
public class FoodDeadLetterTopicHandler {

  private final KafkaService kafkaService;

  @KafkaHandler
  public void processDeadFoodProcessedEvent(@Payload FoodReservationProcessedEvent foodReservationProcessedEvent,
                                            @Header(value = KafkaHeaders.EXCEPTION_FQCN, required = false) String exceptionName,
                                            @Header(value = KafkaHeaders.EXCEPTION_MESSAGE, required = false) String exceptionMessage,
                                            @Header(value = KafkaHeaders.ORIGINAL_TOPIC, required = false) String originalTopic) {

    handleDeadEventsForRetries(foodReservationProcessedEvent, exceptionName, exceptionMessage, originalTopic);
  }

  @KafkaHandler
  public void processDeadFoodReservationOrReleaseFailedEvent(@Payload FoodReservationFailedEvent foodReservationFailedEvent,
                                                             @Header(value = KafkaHeaders.EXCEPTION_FQCN, required = false) String exceptionName,
                                                             @Header(value = KafkaHeaders.EXCEPTION_MESSAGE, required = false) String exceptionMessage,
                                                             @Header(value = KafkaHeaders.ORIGINAL_TOPIC, required = false) String originalTopic) {

    handleDeadEventsForRetries(foodReservationFailedEvent, exceptionName, exceptionMessage, originalTopic);
  }

  @KafkaHandler
  public void processDeadFoodReleaseEvent(@Payload FoodReleaseEvent foodReleaseEvent,
                                          @Header(value = KafkaHeaders.EXCEPTION_FQCN, required = false) String exceptionName,
                                          @Header(value = KafkaHeaders.EXCEPTION_MESSAGE, required = false) String exceptionMessage,
                                          @Header(value = KafkaHeaders.ORIGINAL_TOPIC, required = false) String originalTopic) {

    handleDeadEventsForRetries(foodReleaseEvent, exceptionName, exceptionMessage, originalTopic);
  }

  @KafkaHandler
  public void processDeadFoodReleaseEvent(@Payload FoodReleaseFailedEvent foodReleaseEvent,
                                          @Header(value = KafkaHeaders.EXCEPTION_FQCN, required = false) String exceptionName,
                                          @Header(value = KafkaHeaders.EXCEPTION_MESSAGE, required = false) String exceptionMessage,
                                          @Header(value = KafkaHeaders.ORIGINAL_TOPIC, required = false) String originalTopic) {

    handleDeadEventsForRetries(foodReleaseEvent, exceptionName, exceptionMessage, originalTopic);
  }

  private void handleDeadEventsForRetries(Object event, String exceptionName, String exceptionMessage, String originalTopic) {
    if (Objects.isNull(exceptionMessage) ||
        Objects.isNull(originalTopic)    ||
        ExceptionUtil.noRequeue(exceptionMessage)
    ) {
      LogUtil.deadLetterMissingFormats(exceptionName, exceptionMessage, originalTopic);
      return;
    }

    try {
      Thread.sleep(Integer.parseInt(FIVE_SECONDS));
    } catch (InterruptedException e) {
      throw new RetryableException();
    }

    kafkaService.sendKafkaEvent(originalTopic, event);
  }
}