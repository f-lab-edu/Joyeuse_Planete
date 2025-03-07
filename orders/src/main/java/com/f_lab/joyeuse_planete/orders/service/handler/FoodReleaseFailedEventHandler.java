package com.f_lab.joyeuse_planete.orders.service.handler;

import com.f_lab.joyeuse_planete.core.events.FoodReleaseFailedEvent;
import com.f_lab.joyeuse_planete.core.exceptions.ErrorCodeOrderStatusTranslator;
import com.f_lab.joyeuse_planete.orders.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@KafkaListener(topics = { "${foods.events.topics.release-fail}" }, groupId = "${spring.kafka.consumer.group-id}")
public class FoodReleaseFailedEventHandler {

  private final OrderService orderService;

  @KafkaHandler
  public void process(@Payload FoodReleaseFailedEvent event) {
    orderService.updateOrderStatus(
        event.getOrderId(),
        ErrorCodeOrderStatusTranslator.translate(event.getErrorCode()));
  }
}
