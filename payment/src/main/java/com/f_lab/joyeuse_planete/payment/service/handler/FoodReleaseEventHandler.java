package com.f_lab.joyeuse_planete.payment.service.handler;

import com.f_lab.joyeuse_planete.core.events.FoodReleaseEvent;
import com.f_lab.joyeuse_planete.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@KafkaListener(topics = { "${foods.events.topics.release}" }, groupId = "${spring.kafka.producer.transaction-id-prefix}")
public class FoodReleaseEventHandler {

  private final PaymentService paymentService;

  public void processRefund(@Payload FoodReleaseEvent foodReleaseEvent) {
    paymentService.processRefund(foodReleaseEvent.getPaymentId());
  }
}
