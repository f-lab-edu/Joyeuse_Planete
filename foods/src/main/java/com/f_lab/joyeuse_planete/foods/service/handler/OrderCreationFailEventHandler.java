package com.f_lab.joyeuse_planete.foods.service.handler;

import com.f_lab.joyeuse_planete.core.events.OrderCreationFailedEvent;
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
@KafkaListener(
    topics = { "${payments.events.topic.fail}" },
    groupId = "${spring.kafka.consumer.group-id}"
)
public class OrderCreationFailEventHandler {

  private final FoodService foodService;
  private final KafkaService kafkaService;

  @Value("${foods.events.topic.fail}")
  private String foodProcessFailEvent;

  @KafkaHandler
  public void releaseFoodReservation(@Payload OrderCreationFailedEvent orderCreationFailedEvent) {
    try {
      foodService.release(orderCreationFailedEvent.getFoodId(), orderCreationFailedEvent.getQuantity());
    } catch (Exception e) {
      LogUtil.exception("OrderCreationFailEventHandler.releaseFoodReservation", e);
      throw e;
    }

    sendKafkaOrderCreationFailEvent(orderCreationFailedEvent);
  }

  private void sendKafkaOrderCreationFailEvent(OrderCreationFailedEvent orderCreationFailedEvent) {
    try {
      kafkaService.sendKafkaEvent(foodProcessFailEvent, orderCreationFailedEvent);
    } catch (Exception e) {
      LogUtil.exception("OrderCreatedEventHandler.sendKafkaOrderCreatedEvent", e);
      throw e;
    }
  }
}
