package com.f_lab.joyeuse_planete.orders.service.handler;

import com.f_lab.joyeuse_planete.core.domain.OrderStatus;
import com.f_lab.joyeuse_planete.core.events.RefundProcessedEvent;
import com.f_lab.joyeuse_planete.orders.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@KafkaListener(topics = { "${payment.events.topics.refund}" }, groupId = "${spring.kafka.consumer.group-id}")
public class RefundProcessedEventHandler {

  private final OrderService orderService;

  @KafkaHandler
  public void process(@Payload RefundProcessedEvent event) {
    orderService.updateOrderStatus(event.getOrderId(), OrderStatus.MEMBER_CANCELED);
  }
}
