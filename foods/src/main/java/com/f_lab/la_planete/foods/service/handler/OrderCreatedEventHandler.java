package com.f_lab.la_planete.foods.service.handler;


import com.f_lab.la_planete.core.events.OrderCreatedEvent;
import com.f_lab.la_planete.core.events.OrderCreationFailedEvent;
import com.f_lab.la_planete.core.kafka.aspect.KafkaRetry;
import com.f_lab.la_planete.core.kafka.exceptions.NonRetryableException;
import com.f_lab.la_planete.foods.exceptions.FoodApplicationKafkaRetryableException;
import com.f_lab.la_planete.foods.service.FoodService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@Slf4j
@Component
@RequiredArgsConstructor
@KafkaListener(
    topics = { "${orders.events.topic.name}" },
    groupId = "${spring.kafka.consumer.group-id}"
)
public class OrderCreatedEventHandler {


  private final KafkaTemplate<String, Object> kafkaTemplate;
  private final FoodService foodService;

  @Value("${foods.events.topic.name}")
  private String foodReservationEvent;

  @Value("${foods.events.topic.fail}")
  private String foodProcessFailEvent;

  @KafkaHandler
  @Transactional(value = "jpaTransactionManager")
  public void handleOrderCreatedEvent(@Payload OrderCreatedEvent orderCreatedEvent) {
    try {
      foodService.reserve(orderCreatedEvent.getFoodId(), orderCreatedEvent.getQuantity());
    } catch(LockAcquisitionFailException e) {
      log.error("오류가 발생하였습니다. message = {}", e.getMessage(), e);
      sendOrderCreationFailEvent(orderCreatedEvent);
      throw new NonRetryableException(e);
    }

    try {
      KafkaFoodHandler kafkaFoodHandler = new KafkaFoodHandler(kafkaTemplate);
      kafkaFoodHandler.handleFoodReservationEvent(foodReservationEvent, orderCreatedEvent);
    } catch (Exception e) {
      log.error("오류가 발생하였습니다. message = {}", e.getMessage(), e);
      sendOrderCreationFailEvent(orderCreatedEvent);
      throw e;
    }
  }

  @RequiredArgsConstructor
  static class KafkaFoodHandler {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaRetry
    @Transactional(propagation = REQUIRES_NEW)
    public void handleFoodReservationEvent(String foodReservationEvent, OrderCreatedEvent orderCreatedEvent) {
      try {
        kafkaTemplate.send(foodReservationEvent, orderCreatedEvent);
      } catch(Exception e) {
        log.error("오류가 발생하였습니다. message = {}", e.getMessage(), e);
        throw new FoodApplicationKafkaRetryableException(e);
      }
    }
  }

  private void sendOrderCreationFailEvent(OrderCreatedEvent orderCreatedEvent) {
    kafkaTemplate.send(foodProcessFailEvent, OrderCreationFailedEvent.toEvent(orderCreatedEvent));
  }
}
