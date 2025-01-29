package com.f_lab.joyeuse_planete.foods.service.handler;

import com.f_lab.joyeuse_planete.core.events.OrderCreatedEvent;
import com.f_lab.joyeuse_planete.core.events.OrderCreationFailedEvent;
import com.f_lab.joyeuse_planete.core.kafka.exceptions.NonRetryableException;
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
  public void processDeadLetterTopic(
                                     @Payload OrderCreatedEvent orderCreatedEvent,
                                     @Header(value = KafkaHeaders.EXCEPTION_MESSAGE, required = false) String exceptionMessage,
                                     @Header(value = KafkaHeaders.ORIGINAL_TOPIC, required = false) String originalTopic) {

    log.info("PAYLOAD = {} MESSAGE = {}, TOPIC = {}", orderCreatedEvent, exceptionMessage, originalTopic);

    if (Objects.isNull(exceptionMessage) ||
        Objects.isNull(originalTopic)    ||
        !ExceptionUtil.checkRequeue(exceptionMessage)
    ) {
      return;
    }

    try {
      Thread.sleep(Integer.parseInt(FIVE_SECONDS));
      kafkaService.sendKafkaEvent(originalTopic, orderCreatedEvent);
    } catch (InterruptedException e) {
      throw new RetryableException();
    }
  }
}
