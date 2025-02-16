package com.f_lab.joyeuse_planete.orders.service.handler;

import com.f_lab.joyeuse_planete.core.domain.OrderStatus;
import com.f_lab.joyeuse_planete.core.events.FoodReservationProcessedEvent;
import com.f_lab.joyeuse_planete.orders.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@KafkaListener(topics = { "${foods.events.topics.reserve}" }, groupId = "${spring.kafka.consumer.group-id}")
public class FoodReservationProcessedEventHandler {

  private final OrderService orderService;

  @KafkaHandler
  public void processFoodReservationProcessedEvent(@Payload FoodReservationProcessedEvent foodReservationProcessedEvent) {
    orderService.updateOrderStatus(foodReservationProcessedEvent.getOrderId(), OrderStatus.READY_FOR_PAYMENT);
  }
}
