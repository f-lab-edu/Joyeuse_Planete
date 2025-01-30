package com.f_lab.joyeuse_planete.foods.service.handler;


import com.f_lab.joyeuse_planete.core.events.OrderCreatedEvent;
import com.f_lab.joyeuse_planete.core.events.OrderCreationFailedEvent;
import com.f_lab.joyeuse_planete.core.kafka.service.KafkaService;
import com.f_lab.joyeuse_planete.core.util.log.LogUtil;
import com.f_lab.joyeuse_planete.foods.service.FoodService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.DisconnectException;
import org.apache.kafka.common.errors.NetworkException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
@RequiredArgsConstructor
@Transactional("transactionManager")
@KafkaListener(
    topics = { "${orders.events.topic.name}" },
    groupId = "${spring.kafka.consumer.group-id}"
)
public class OrderCreatedEventHandler {


  private final FoodService foodService;
  private final KafkaService kafkaService;

  @Value("${foods.events.topic.name}")
  private String foodReservationEvent;

  @Value("${foods.events.topic.fail}")
  private String foodProcessFailEvent;

  @KafkaHandler
  public void reserveFoodAfterOrderCreatedEvent(@Payload OrderCreatedEvent orderCreatedEvent) {
    try {
      foodService.reserve(orderCreatedEvent.getFoodId(), orderCreatedEvent.getQuantity());
    } catch(Exception e) {
      LogUtil.exception("OrderCreatedEventHandler.reserveFoodAfterOrderCreatedEvent", e);
      kafkaService.sendKafkaEvent(foodProcessFailEvent, OrderCreationFailedEvent.toEvent(orderCreatedEvent));

      throw e;
    }

    sendKafkaOrderCreatedEvent(orderCreatedEvent);
  }

  private void sendKafkaOrderCreatedEvent(OrderCreatedEvent orderCreatedEvent) {
    try {
      kafkaService.sendKafkaEvent(foodReservationEvent, orderCreatedEvent);
    } catch (Exception e) {
      LogUtil.exception("OrderCreatedEventHandler.sendKafkaOrderCreatedEvent", e);
      kafkaService.sendKafkaEvent(foodProcessFailEvent, OrderCreationFailedEvent.toEvent(orderCreatedEvent));

      throw e;
    }
  }
}
