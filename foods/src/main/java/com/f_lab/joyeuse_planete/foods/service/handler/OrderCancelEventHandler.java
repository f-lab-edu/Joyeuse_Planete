package com.f_lab.joyeuse_planete.foods.service.handler;


import com.f_lab.joyeuse_planete.core.annotation.Retry;
import com.f_lab.joyeuse_planete.core.events.EventToEventMapper;
import com.f_lab.joyeuse_planete.core.events.FoodReleaseEvent;
import com.f_lab.joyeuse_planete.core.events.OrderCancelEvent;
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
@KafkaListener(topics = { "${orders.events.topics.cancel}" }, groupId = "${spring.kafka.producer.transaction-id-prefix}")
public class OrderCancelEventHandler {

  private final FoodService foodService;
  private final ApplicationEventPublisher eventPublisher;

  @Retry
  @Transactional
  @KafkaHandler
  public void handleOrderCancelEvent(@Payload OrderCancelEvent orderCancelEvent) {
    try {
      foodService.release(orderCancelEvent.getFoodId(), orderCancelEvent.getQuantity());
      eventPublisher.publishEvent(FoodReleaseEvent.toEvent(orderCancelEvent));

    } catch(JoyeusePlaneteApplicationException e) {
      LogUtil.exception("OrderCancelEventHandler.handleOrderCancelEvent", e);
      eventPublisher.publishEvent(EventToEventMapper.mapToCompensationEvent(orderCancelEvent, e.getErrorCode()));

      throw e;
    } catch (Exception e) {
      LogUtil.exception("OrderCancelEventHandler.handleOrderCancelEvent", e);
      eventPublisher.publishEvent(EventToEventMapper.mapToCompensationEvent(orderCancelEvent, ErrorCode.UNKNOWN_EXCEPTION));

      throw new TransactionRollbackException(e);
    }
  }
}
