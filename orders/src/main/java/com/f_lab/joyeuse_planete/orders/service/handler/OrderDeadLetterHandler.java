package com.f_lab.joyeuse_planete.orders.service.handler;

import com.f_lab.joyeuse_planete.core.events.OrderCancelEvent;
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

import static com.f_lab.joyeuse_planete.core.util.time.TimeConstants.TimeConstantsString.FIVE_SECONDS;


@Component
@RequiredArgsConstructor
@KafkaListener(topics = { "${orders.dead-letter-topic}" }, groupId = "${spring.kafka.consumer.group-id}")
public class OrderDeadLetterHandler {

  private final KafkaService kafkaService;

  @KafkaHandler
  public void handle(@Payload OrderCancelEvent orderCancellationEvent,
                     @Header(value = KafkaHeaders.EXCEPTION_FQCN, required = false) String exceptionName,
                     @Header(value = KafkaHeaders.EXCEPTION_MESSAGE, required = false) String exceptionMessage,
                     @Header(value = KafkaHeaders.ORIGINAL_TOPIC, required = false) String originalTopic) {


    if (Objects.isNull(exceptionName) ||
        Objects.isNull(exceptionMessage) ||
        ExceptionUtil.noRequeue(exceptionMessage)
    ) {
      LogUtil.deadLetterMissingFormats(exceptionName, exceptionMessage, originalTopic);
      return;
    }

    sleep(FIVE_SECONDS);

    kafkaService.sendKafkaEvent(originalTopic, orderCancellationEvent);
  }

  private void sleep(String seconds) {
    try {
      Thread.sleep(Integer.parseInt(seconds));
    } catch (Throwable e) {
      throw new RetryableException();
    }
  }
}
