package com.f_lab.la_planete.foods.service.handler;


import com.f_lab.la_planete.core.events.OrderCreatedEvent;
import com.f_lab.la_planete.core.events.OrderCreationFailedEvent;
import com.f_lab.la_planete.core.exceptions.LockAcquisitionFailException;
import com.f_lab.la_planete.core.kafka.aspect.KafkaRetry;
import com.f_lab.la_planete.core.kafka.exceptions.NonRetryableException;
import com.f_lab.la_planete.core.kafka.exceptions.RetryableException;
import com.f_lab.la_planete.foods.exceptions.FoodServiceHandlerFailException;
import com.f_lab.la_planete.foods.service.FoodService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
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


  private final KafkaTemplate<String, Object> kafkaTemplate;
  private final FoodService foodService;

  @Value("${foods.events.topic.name}")
  private String foodReservationEvent;

  @Value("${foods.events.topic.fail}")
  private String foodProcessFailEvent;

  @KafkaRetry
  @KafkaHandler
  @Transactional(value = "jpaTransactionManager")
  public void handleOrderCreatedEvent(@Payload OrderCreatedEvent orderCreatedEvent) {
    try {
      foodService.reserve(orderCreatedEvent.getFoodId(), orderCreatedEvent.getQuantity());
      kafkaTemplate.send(foodReservationEvent, orderCreatedEvent);

      // 기존의 NonRetryableException 에 속하는 모든 하위 예외와 락 획들 실패 예외는 더이상 시도 하지 않음
    } catch(NonRetryableException | LockAcquisitionFailException e) {
      log.error("오류가 발생하였습니다. message = {}", e.getMessage(), e);
      throw new NonRetryableException(e);

    } catch(Exception e) {
      kafkaTemplate.send(foodProcessFailEvent, OrderCreationFailedEvent.toEvent(orderCreatedEvent));
      throw new FoodServiceHandlerFailException(e);
    }
  }
}
