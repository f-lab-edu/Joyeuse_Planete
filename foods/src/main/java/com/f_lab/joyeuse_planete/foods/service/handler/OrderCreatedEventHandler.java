package com.f_lab.joyeuse_planete.foods.service.handler;


import com.f_lab.joyeuse_planete.core.events.FoodReservationOrReleaseFailedEvent;
import com.f_lab.joyeuse_planete.core.events.FoodReservationProcessedEvent;
import com.f_lab.joyeuse_planete.core.events.OrderCreatedEvent;
import com.f_lab.joyeuse_planete.core.exceptions.ErrorCode;
import com.f_lab.joyeuse_planete.core.exceptions.JoyeusePlaneteApplicationException;
import com.f_lab.joyeuse_planete.core.kafka.service.KafkaService;
import com.f_lab.joyeuse_planete.core.util.log.LogUtil;
import com.f_lab.joyeuse_planete.foods.service.FoodService;

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
@KafkaListener(topics = { "${orders.events.topics.create}" }, groupId = "${spring.kafka.consumer.group-id}")
public class OrderCreatedEventHandler {

  private final FoodService foodService;
  private final KafkaService kafkaService;

  @Value("${foods.events.topics.reserve}")
  private String FOOD_RESERVATION_EVENT;

  @Value("${foods.events.topics.reserve-fail}")
  private String FOOD_RESERVATION_FAIL_EVENT;

  @KafkaHandler
  public void reserveFoodAfterOrderCreatedEvent(@Payload OrderCreatedEvent orderCreatedEvent) {
    try {
      foodService.reserve(orderCreatedEvent.getFoodId(), orderCreatedEvent.getQuantity());
    } catch (JoyeusePlaneteApplicationException e) {
      LogUtil.exception("OrderCreatedEventHandler.reserveFoodAfterOrderCreatedEvent", e);
      kafkaService.sendKafkaEvent(FOOD_RESERVATION_FAIL_EVENT, FoodReservationOrReleaseFailedEvent.toEvent(orderCreatedEvent, e.getErrorCode()));

      throw e;

    } catch(Exception e) {
      LogUtil.exception("OrderCreatedEventHandler.reserveFoodAfterOrderCreatedEvent", e);
      kafkaService.sendKafkaEvent(FOOD_RESERVATION_FAIL_EVENT, FoodReservationOrReleaseFailedEvent.toEvent(orderCreatedEvent, ErrorCode.UNKNOWN_EXCEPTION));

      throw e;
    }

    sendKafkaFoodReservationEvent(FoodReservationProcessedEvent.toEvent(orderCreatedEvent));
  }

  private void sendKafkaFoodReservationEvent(FoodReservationProcessedEvent foodReservationProcessedEvent) {
    try {
      kafkaService.sendKafkaEvent(FOOD_RESERVATION_EVENT, foodReservationProcessedEvent);
    } catch (Exception e) {
      LogUtil.exception("OrderCreatedEventHandler.sendKafkaFoodReservationEvent", e);
    }
  }
}
