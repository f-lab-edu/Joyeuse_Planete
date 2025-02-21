package com.f_lab.joyeuse_planete.orders.service.handler;

import com.f_lab.joyeuse_planete.core.events.FoodReservationFailedEvent;
import com.f_lab.joyeuse_planete.core.exceptions.ErrorCodeOrderStatusTranslator;
import com.f_lab.joyeuse_planete.orders.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@KafkaListener(topics = { "${foods.events.topics.reserve-fail}" }, groupId = "${spring.kafka.consumer.group-id}")
public class FoodReservationFailedEventHandler {

  private final OrderService orderService;

  @KafkaHandler
  public void processFoodReservationFailEvent(@Payload FoodReservationFailedEvent foodReservationFailedEvent) {
    orderService.updateOrderStatus(
        foodReservationFailedEvent.getOrderId(),
        ErrorCodeOrderStatusTranslator.translate(foodReservationFailedEvent.getErrorCode()));
  }
}
