package com.f_lab.joyeuse_planete.payment.service.handler;

import com.f_lab.joyeuse_planete.core.events.OrderCreatedEvent;

import com.f_lab.joyeuse_planete.core.kafka.service.KafkaService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
@KafkaListener(topics = "${payments.dead-letter-topic.name}", groupId = "${spring.kafka.consumer.group-id}")
public class PaymentDeadLetterTopicHandler {

  private final KafkaService kafkaService;

  @KafkaHandler
  public void processDeadOrderCreatedEvent(@Payload OrderCreatedEvent orderCreatedEvent,
                                           @Header(value = KafkaHeaders.EXCEPTION_FQCN, required = false) String exceptionName,
                                           @Header(value = KafkaHeaders.EXCEPTION_MESSAGE, required = false) String exceptionMessage,
                                           @Header(value = KafkaHeaders.ORIGINAL_TOPIC, required = false) String originalTopic) {

    // TODO: THINK ABOUT THE LOGICS;
  }
}
