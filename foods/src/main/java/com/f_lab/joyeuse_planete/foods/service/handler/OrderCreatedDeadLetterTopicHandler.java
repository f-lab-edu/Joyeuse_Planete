package com.f_lab.joyeuse_planete.foods.service.handler;

import com.f_lab.joyeuse_planete.core.events.OrderCreatedEvent;
import com.f_lab.joyeuse_planete.core.events.OrderCreationFailedEvent;
import com.f_lab.joyeuse_planete.core.exceptions.ErrorCode;
import com.f_lab.joyeuse_planete.core.exceptions.JoyeusePlaneteApplicationException;
import com.f_lab.joyeuse_planete.core.kafka.exceptions.RetryableException;
import com.f_lab.joyeuse_planete.core.kafka.service.KafkaService;
import com.f_lab.joyeuse_planete.core.kafka.util.ExceptionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.f_lab.joyeuse_planete.core.util.time.TimeConstantsString.FIVE_SECONDS;


@Slf4j
@Component
@RequiredArgsConstructor
@KafkaListener(topics = "${foods.dead-letter-topic.name}", groupId = "${spring.kafka.consumer.group-id}")
public class OrderCreatedDeadLetterTopicHandler {

  private final KafkaService kafkaService;

  @KafkaHandler
  public void processDeadOrderCreatedEvent(@Payload OrderCreatedEvent orderCreatedEvent,
                                           @Header(value = KafkaHeaders.EXCEPTION_FQCN, required = false) String exceptionName,
                                           @Header(value = KafkaHeaders.EXCEPTION_MESSAGE, required = false) String exceptionMessage,
                                           @Header(value = KafkaHeaders.ORIGINAL_TOPIC, required = false) String originalTopic) {

    // TODO: THINK ABOUT THE LOGICS;
  }

  @KafkaHandler
  public void processDeadOrderCreationFailedEvent(@Payload OrderCreationFailedEvent orderCreationFailedEvent,
                                                  @Header(value = KafkaHeaders.EXCEPTION_FQCN, required = false) String exceptionName,
                                                  @Header(value = KafkaHeaders.EXCEPTION_MESSAGE, required = false) String exceptionMessage,
                                                  @Header(value = KafkaHeaders.ORIGINAL_TOPIC, required = false) String originalTopic
  ) {
    if (Objects.isNull(exceptionMessage) ||
        Objects.isNull(originalTopic)    ||
        ExceptionUtil.noRequeue(exceptionMessage)
    ) {
      return;
    }

    //TODO: 추후에 exponential 하게 구현할 수 있음
    try {
      Thread.sleep(Integer.parseInt(FIVE_SECONDS));
    } catch (InterruptedException e) {
      throw new RetryableException();
    }

    try {
      kafkaService.sendKafkaEvent(originalTopic, orderCreationFailedEvent);
    } catch(Exception e) {
      throw new JoyeusePlaneteApplicationException(ErrorCode.KAFKA_DEAD_LETTER_TOPIC_FAIL_EXCEPTION);
    }
  }
}