package com.f_lab.joyeuse_planete.foods.service.listener;

import com.f_lab.joyeuse_planete.core.events.EventToEventMapper;
import com.f_lab.joyeuse_planete.core.events.FoodReleaseEvent;
import com.f_lab.joyeuse_planete.core.events.FoodReleaseFailedEvent;
import com.f_lab.joyeuse_planete.core.events.FoodReservationFailedEvent;
import com.f_lab.joyeuse_planete.core.events.FoodReservationProcessedEvent;
import com.f_lab.joyeuse_planete.core.exceptions.ErrorCode;
import com.f_lab.joyeuse_planete.core.kafka.service.KafkaService;
import com.f_lab.joyeuse_planete.core.util.log.LogUtil;
import com.f_lab.joyeuse_planete.foods.service.FoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;
import static org.springframework.transaction.event.TransactionPhase.AFTER_ROLLBACK;

@Component
@RequiredArgsConstructor
public class FoodEventListener {

  private final KafkaService kafkaService;
  private final FoodService foodService;
  private final ApplicationEventPublisher eventPublisher;

  @Value("${foods.events.topics.reserve}")
  private String FOOD_RESERVATION_EVENT;

  @Value("${foods.events.topics.reserve-fail}")
  private String FOOD_RESERVATION_FAIL_EVENT;

  @Value("${foods.events.topics.release}")
  private String FOOD_RELEASE_EVENT;

  @Value("${foods.events.topics.release-fail}")
  private String FOOD_RELEASE_FAIL_EVENT;

  @Async
  @TransactionalEventListener(phase = AFTER_COMMIT)
  public void on(FoodReservationProcessedEvent event) {
    try {
      kafkaService.sendKafkaEvent(FOOD_RESERVATION_EVENT, event);

    } catch (Exception e) {
      LogUtil.exception("FoodEventListener.on (FoodReservationProcessedEvent)", e);
      foodService.release(event.getFoodId(), event.getQuantity());

      eventPublisher.publishEvent(
          EventToEventMapper.mapToCompensationEvent(event, ErrorCode.UNKNOWN_EXCEPTION));
    }
  }

  @Async
  @TransactionalEventListener(phase = AFTER_ROLLBACK)
  public void on(FoodReservationFailedEvent event) {
    try {
      kafkaService.sendKafkaEvent(FOOD_RESERVATION_FAIL_EVENT, event);

    } catch (Exception e) {
      LogUtil.exception("FoodEventListener.on (FoodReservationFailedEvent)", e);
      LogUtil.info("FoodEventListener.on (FoodReservationFailedEvent)", event);
    }
  }

  @Async
  @TransactionalEventListener(phase = AFTER_COMMIT)
  public void on(FoodReleaseEvent event) {
    try {
      kafkaService.sendKafkaEvent(FOOD_RELEASE_EVENT, event);

    } catch (Exception e) {
      LogUtil.exception("FoodEventListener.on (FoodReleaseEvent)", e);

      eventPublisher.publishEvent(
          EventToEventMapper.mapToCompensationEvent(event, ErrorCode.FOOD_RELEASE_KAFKA_FAIL_EVENT));
    }
  }

  @Async
  @TransactionalEventListener(phase = AFTER_ROLLBACK)
  public void on(FoodReleaseFailedEvent event) {
    try {
      kafkaService.sendKafkaEvent(FOOD_RELEASE_FAIL_EVENT, event);

    } catch (Exception e) {
      LogUtil.exception("FoodEventListener.on (FoodReleaseFailedEvent)", e);
      LogUtil.info("FoodEventListener.on (FoodReleaseFailedEvent)", event);
    }
  }
}
