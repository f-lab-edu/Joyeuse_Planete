package com.f_lab.joyeuse_planete.foods.service.handler;

import com.f_lab.joyeuse_planete.core.events.EventToEventMapper;
import com.f_lab.joyeuse_planete.core.events.FoodReservationProcessedEvent;
import com.f_lab.joyeuse_planete.core.events.OrderCreatedEvent;
import com.f_lab.joyeuse_planete.core.exceptions.ErrorCode;
import com.f_lab.joyeuse_planete.core.exceptions.JoyeusePlaneteApplicationException;
import com.f_lab.joyeuse_planete.core.exceptions.TransactionRollbackException;
import com.f_lab.joyeuse_planete.core.util.log.LogUtil;
import com.f_lab.joyeuse_planete.foods.exceptions.AlreadyProcessedEventException;
import com.f_lab.joyeuse_planete.foods.service.EventDuplicateCheckService;
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
  private final EventDuplicateCheckService eventDuplicateCheckService;

  @Transactional
  @KafkaHandler
  public void reserveFoodAfterOrderCreatedEvent(@Payload OrderCreatedEvent orderCreatedEvent) {
    try {
      foodService.reserve(orderCreatedEvent.getFoodId(), orderCreatedEvent.getQuantity());
      eventDuplicateCheckService.writeReserveEventLog(orderCreatedEvent);
      eventPublisher.publishEvent(FoodReservationProcessedEvent.toEvent(orderCreatedEvent));

    } catch(AlreadyProcessedEventException e) {
      LogUtil.exception("OrderCreatedEventHandler.reserveFoodAfterOrderCreatedEvent (AlreadyProcessedEventException)", e);

      throw new TransactionRollbackException(e);
    } catch (JoyeusePlaneteApplicationException e) {
      LogUtil.exception("OrderCreatedEventHandler.reserveFoodAfterOrderCreatedEvent (JoyeusePlaneteApplicationException)", e);
      eventPublisher.publishEvent(EventToEventMapper.mapToCompensationEvent(orderCreatedEvent, e.getErrorCode()));

      throw e;
    } catch(Exception e) {
      System.out.println("여기2");
      LogUtil.exception("OrderCreatedEventHandler.reserveFoodAfterOrderCreatedEvent (Exception)", e);
      eventPublisher.publishEvent(EventToEventMapper.mapToCompensationEvent(orderCreatedEvent, ErrorCode.UNKNOWN_EXCEPTION));

      throw new TransactionRollbackException(e);
    }
  }
}
