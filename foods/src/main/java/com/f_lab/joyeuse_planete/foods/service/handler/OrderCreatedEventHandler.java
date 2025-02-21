package com.f_lab.joyeuse_planete.foods.service.handler;

import com.f_lab.joyeuse_planete.core.events.FoodReservationFailedEvent;
import com.f_lab.joyeuse_planete.core.events.OrderCreatedEvent;
import com.f_lab.joyeuse_planete.core.exceptions.ErrorCode;
import com.f_lab.joyeuse_planete.core.exceptions.JoyeusePlaneteApplicationException;
import com.f_lab.joyeuse_planete.core.exceptions.TransactionRollbackException;
import com.f_lab.joyeuse_planete.core.util.log.LogUtil;
import com.f_lab.joyeuse_planete.foods.service.FoodService;

import lombok.RequiredArgsConstructor;

import org.springframework.context.ApplicationEventPublisher;
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
  private final ApplicationEventPublisher eventPublisher;

  @KafkaHandler
  public void reserveFoodAfterOrderCreatedEvent(@Payload OrderCreatedEvent orderCreatedEvent) {
    try {
      foodService.reserve(orderCreatedEvent.getFoodId(), orderCreatedEvent.getQuantity());
    } catch (JoyeusePlaneteApplicationException e) {
      LogUtil.exception("OrderCreatedEventHandler.reserveFoodAfterOrderCreatedEvent", e);
      eventPublisher.publishEvent(FoodReservationFailedEvent.toEvent(orderCreatedEvent, e.getErrorCode()));

      throw e;
    } catch(Exception e) {
      LogUtil.exception("OrderCreatedEventHandler.reserveFoodAfterOrderCreatedEvent", e);
      eventPublisher.publishEvent(FoodReservationFailedEvent.toEvent(orderCreatedEvent, ErrorCode.UNKNOWN_EXCEPTION));

      throw new TransactionRollbackException(e);
    }
  }
}
