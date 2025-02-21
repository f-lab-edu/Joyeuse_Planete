package com.f_lab.joyeuse_planete.foods.service.listener;

import com.f_lab.joyeuse_planete.core.events.FoodReleaseEvent;
import com.f_lab.joyeuse_planete.core.events.FoodReleaseFailedEvent;
import com.f_lab.joyeuse_planete.core.events.FoodReservationFailedEvent;
import com.f_lab.joyeuse_planete.core.events.FoodReservationProcessedEvent;
import com.f_lab.joyeuse_planete.core.kafka.service.KafkaService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;
import static org.springframework.transaction.event.TransactionPhase.AFTER_ROLLBACK;

@Component
@RequiredArgsConstructor
public class FoodEventListener {

  private final KafkaService kafkaService;

  @Value("${foods.events.topics.reserve}")
  private String FOOD_RESERVATION_EVENT;

  @Value("${foods.events.topics.reserve-fail}")
  private String FOOD_RESERVATION_FAIL_EVENT;

  @Value("${foods.events.topics.release}")
  private String FOOD_RELEASE_EVENT;

  @Value("${foods.events.topics.release-fail}")
  private String FOOD_RELEASE_FAIL_EVENT;

  // TODO apply @Async
  @TransactionalEventListener(phase = AFTER_COMMIT)
  public void on(FoodReservationProcessedEvent event) {
    kafkaService.sendKafkaEvent(FOOD_RESERVATION_EVENT, event);
  }

  @TransactionalEventListener(phase = AFTER_ROLLBACK)
  public void on(FoodReservationFailedEvent event) {
    kafkaService.sendKafkaEvent(FOOD_RESERVATION_FAIL_EVENT, event);
  }

  @TransactionalEventListener(phase = AFTER_COMMIT)
  public void on(FoodReleaseEvent event) {
    kafkaService.sendKafkaEvent(FOOD_RELEASE_EVENT, event);
  }

  @TransactionalEventListener(phase = AFTER_ROLLBACK)
  public void on(FoodReleaseFailedEvent event) {
    kafkaService.sendKafkaEvent(FOOD_RELEASE_FAIL_EVENT, event);
  }
}
