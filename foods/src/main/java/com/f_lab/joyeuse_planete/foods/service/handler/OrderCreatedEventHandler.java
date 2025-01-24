package com.f_lab.joyeuse_planete.foods.service.handler;


import com.f_lab.joyeuse_planete.core.events.OrderCreatedEvent;
import com.f_lab.joyeuse_planete.core.events.OrderCreationFailedEvent;
import com.f_lab.joyeuse_planete.foods.service.FoodService;
import com.f_lab.joyeuse_planete.foods.service.KafkaHandlerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Component
@RequiredArgsConstructor
@KafkaListener(
    topics = { "${orders.events.topic.name}" },
    groupId = "${spring.kafka.consumer.group-id}"
)
public class OrderCreatedEventHandler {


  private final FoodService foodService;
  private final KafkaHandlerService kafkaHandlerService;

  @Value("${foods.events.topic.name}")
  private String foodReservationEvent;

  @Value("${foods.events.topic.fail}")
  private String foodProcessFailEvent;

  @KafkaHandler
  @Transactional(value = "jpaTransactionManager")
  public void reserveFoodAfterOrderCreatedEvent(@Payload OrderCreatedEvent orderCreatedEvent) {
    try {
      foodService.reserve(orderCreatedEvent.getFoodId(), orderCreatedEvent.getQuantity());
    } catch(Exception e) {
      log.error("오류가 발생하였습니다. message = {}", e.getMessage(), e);
      kafkaHandlerService.sendKafkaFailureEvent(
          foodProcessFailEvent,
          OrderCreationFailedEvent.toEvent(orderCreatedEvent)
      );

      throw e;
    }

    sendKafkaEvent(orderCreatedEvent);
  }

  private void sendKafkaEvent(OrderCreatedEvent orderCreatedEvent) {
    try {
      kafkaHandlerService.sendKafkaEvent(foodReservationEvent, orderCreatedEvent);
    } catch (Exception e) {
      log.error("오류가 발생하였습니다. message = {}", e.getMessage(), e);
      kafkaHandlerService.sendKafkaFailureEvent(
          foodProcessFailEvent,
          OrderCreationFailedEvent.toEvent(orderCreatedEvent)
      );

      throw e;
    }
  }
}
