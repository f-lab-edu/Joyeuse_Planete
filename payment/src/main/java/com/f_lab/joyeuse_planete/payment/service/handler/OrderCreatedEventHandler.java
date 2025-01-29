package com.f_lab.joyeuse_planete.payment.service.handler;

import com.f_lab.joyeuse_planete.core.events.OrderCreatedEvent;
import com.f_lab.joyeuse_planete.core.events.OrderCreationFailedEvent;
import com.f_lab.joyeuse_planete.core.exceptions.JoyeusePlaneteApplicationException;
import com.f_lab.joyeuse_planete.core.kafka.service.KafkaService;
import com.f_lab.joyeuse_planete.core.util.log.LogUtil;
import com.f_lab.joyeuse_planete.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
@RequiredArgsConstructor
@Transactional("transactionManager")
@KafkaListener(
    topics = { "${foods.events.topic.name}" },
    groupId = "${spring.kafka.consumer.group-id}"
)
public class OrderCreatedEventHandler {

  private final PaymentService paymentService;
  private final KafkaService kafkaService;

  @Value("${payments.events.topic.name}")
  private String paymentCompleteEvent;

  @Value("${payments.events.topic.fail}")
  private String paymentProcessingFailEvent;

  @KafkaHandler
  public void processPaymentAfterFoodProcessing(@Payload OrderCreatedEvent orderCreatedEvent) {
    try {
      paymentService.process();
    } catch (Exception e) {
      LogUtil.exception("OrderCreatedEventHandler.processPaymentAfterFoodProcessing", e);
      kafkaService.sendKafkaEvent(paymentProcessingFailEvent, OrderCreationFailedEvent.toEvent(orderCreatedEvent));

      throw e;
    }

    sendKafkaOrderCreationEvent(orderCreatedEvent);
  }

  private void sendKafkaOrderCreationEvent(OrderCreatedEvent orderCreatedEvent) {
    try {
      kafkaService.sendKafkaEvent(paymentCompleteEvent, orderCreatedEvent);
    } catch (Exception e) {
      LogUtil.exception("OrderCreatedEventHandler.sendKafkaOrderCreationEvent", e);
      kafkaService.sendKafkaEvent(paymentProcessingFailEvent, OrderCreationFailedEvent.toEvent(orderCreatedEvent));

      throw e;
    }
  }
}
