package com.f_lab.joyeuse_planete.orders.service.handler;

import com.f_lab.joyeuse_planete.core.domain.OrderStatus;
import com.f_lab.joyeuse_planete.core.events.PaymentProcessedEvent;
import com.f_lab.joyeuse_planete.orders.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@KafkaListener(topics = { "${payment.events.topics.process}" }, groupId = "${spring.kafka.consumer.group-id}")
public class PaymentProcessedEventHandler {

  private final OrderService orderService;

  @KafkaHandler
  public void process(@Payload PaymentProcessedEvent event) {
    orderService.updateOrderStatus(event.getOrderId(), OrderStatus.DONE);
  }
}
