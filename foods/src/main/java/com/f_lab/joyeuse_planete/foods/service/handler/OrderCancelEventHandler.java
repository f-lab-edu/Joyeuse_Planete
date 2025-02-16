package com.f_lab.joyeuse_planete.foods.service.handler;


import com.f_lab.joyeuse_planete.core.events.FoodReleaseEvent;
import com.f_lab.joyeuse_planete.core.events.FoodReservationOrReleaseFailedEvent;
import com.f_lab.joyeuse_planete.core.events.OrderCancelEvent;
import com.f_lab.joyeuse_planete.core.exceptions.ErrorCode;
import com.f_lab.joyeuse_planete.core.exceptions.JoyeusePlaneteApplicationException;
import com.f_lab.joyeuse_planete.core.kafka.service.KafkaService;
import com.f_lab.joyeuse_planete.core.util.log.LogUtil;
import com.f_lab.joyeuse_planete.foods.service.FoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@KafkaListener(topics = { "${orders.events.topics.cancel}" }, groupId = "${spring.kafka.producer.transaction-id-prefix}")
public class OrderCancelEventHandler {

  private final FoodService foodService;
  private final KafkaService kafkaService;

  @Value("${foods.events.topics.release}")
  private String FOOD_RELEASE_EVENT;

  @Value("${foods.events.topics.release-fail}")
  private String FOOD_RELEASE_FAIL_EVENT;

  @Transactional
  @KafkaHandler
  public void handleOrderCancelEvent(OrderCancelEvent orderCancelEvent) {
    try {
      foodService.release(orderCancelEvent.getFoodId(), orderCancelEvent.getQuantity());
    } catch(JoyeusePlaneteApplicationException e) {
      LogUtil.exception("OrderCancelEventHandler.handleOrderCancelEvent", e);
      kafkaService.sendKafkaEvent(FOOD_RELEASE_FAIL_EVENT, FoodReservationOrReleaseFailedEvent.toEvent(orderCancelEvent, e.getErrorCode()));

      throw e;
    } catch (Exception e) {
      LogUtil.exception("OrderCancelEventHandler.handleOrderCancelEvent", e);
      kafkaService.sendKafkaEvent(FOOD_RELEASE_FAIL_EVENT, FoodReservationOrReleaseFailedEvent.toEvent(orderCancelEvent, ErrorCode.UNKNOWN_EXCEPTION));

      throw new RuntimeException(e);
    }

    sendKafkaFoodReleaseEvent(FoodReleaseEvent.toEvent(orderCancelEvent));
  }

  private void sendKafkaFoodReleaseEvent(FoodReleaseEvent foodReleaseEvent) {
    try {
      kafkaService.sendKafkaEvent(FOOD_RELEASE_EVENT, foodReleaseEvent);
    } catch (Exception e) {
      LogUtil.exception("OrderCancelEventHandler.sendKafkaFoodReleaseEvent", e);
    }
  }
}
