package com.f_lab.joyeuse_planete.orders.service.listener;


import com.f_lab.joyeuse_planete.core.events.OrderCancelEvent;
import com.f_lab.joyeuse_planete.core.events.OrderCreatedEvent;
import com.f_lab.joyeuse_planete.core.kafka.service.KafkaService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@Component
@RequiredArgsConstructor
public class OrderEventListener {

  private final KafkaService kafkaService;

  @Value("${orders.events.topics.create}")
  String ORDER_CREATED_EVENT;

  @Value("${orders.events.topics.cancel}")
  String ORDER_CANCELLATION_EVENT;

  @TransactionalEventListener(phase = AFTER_COMMIT)
  public void on(OrderCreatedEvent event) {
    kafkaService.sendKafkaEvent(ORDER_CREATED_EVENT, event);
  }

  @TransactionalEventListener(phase = AFTER_COMMIT)
  public void on(OrderCancelEvent event) {
    kafkaService.sendKafkaEvent(ORDER_CANCELLATION_EVENT, event);
  }
}
