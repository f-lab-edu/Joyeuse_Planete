package com.f_lab.joyeuse_planete.payment.service.handler;


import com.f_lab.joyeuse_planete.core.events.PaymentProcessedEvent;
import com.f_lab.joyeuse_planete.core.events.PaymentProcessingFailedEvent;
import com.f_lab.joyeuse_planete.core.kafka.exceptions.RetryableException;
import com.f_lab.joyeuse_planete.core.kafka.service.KafkaService;

import com.f_lab.joyeuse_planete.core.kafka.util.ExceptionUtil;
import com.f_lab.joyeuse_planete.core.util.log.LogUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.f_lab.joyeuse_planete.core.util.time.TimeConstantsString.FIVE_SECONDS;


@Component
@RequiredArgsConstructor
@KafkaListener(topics = "${payment.dead-letter-topic}", groupId = "${spring.kafka.consumer.group-id}")
public class PaymentDeadLetterTopicHandler {

  private final KafkaService kafkaService;

  @KafkaHandler
  public void processDeadPaymentProcessedEvent(@Payload PaymentProcessedEvent paymentProcessedEvent,
                                               @Header(value = KafkaHeaders.EXCEPTION_FQCN, required = false) String exceptionName,
                                               @Header(value = KafkaHeaders.EXCEPTION_MESSAGE, required = false) String exceptionMessage,
                                               @Header(value = KafkaHeaders.ORIGINAL_TOPIC, required = false) String originalTopic) {

    handleDeadEventsForRetries(paymentProcessedEvent, exceptionName, exceptionMessage, originalTopic);
  }

  @KafkaHandler
  public void processDeadPaymentProcessingFailedEvent(@Payload PaymentProcessingFailedEvent paymentProcessingFailedEvent,
                                                      @Header(value = KafkaHeaders.EXCEPTION_FQCN, required = false) String exceptionName,
                                                      @Header(value = KafkaHeaders.EXCEPTION_MESSAGE, required = false) String exceptionMessage,
                                                      @Header(value = KafkaHeaders.ORIGINAL_TOPIC, required = false) String originalTopic) {

    handleDeadEventsForRetries(paymentProcessingFailedEvent, exceptionName, exceptionMessage, originalTopic);
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
