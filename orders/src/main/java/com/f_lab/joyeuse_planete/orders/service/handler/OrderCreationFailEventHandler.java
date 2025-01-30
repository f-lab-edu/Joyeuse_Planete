package com.f_lab.joyeuse_planete.orders.service.handler;

import com.f_lab.joyeuse_planete.core.domain.OrderStatus;
import com.f_lab.joyeuse_planete.core.events.OrderCreationFailedEvent;
import com.f_lab.joyeuse_planete.orders.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
@RequiredArgsConstructor
@Transactional("transactionManager")
@KafkaListener(
    topics = { "${foods.events.topic.fail}" },
    groupId = "${spring.kafka.consumer.group-id}"
)
public class OrderCreationFailEventHandler {

  private final OrderService orderService;

  @KafkaHandler
  public void processOrderCreationFailEvent(@Payload OrderCreationFailedEvent orderCreationFailedEvent) {
    orderService.updateOrderStatus(orderCreationFailedEvent.getOrderId(), OrderStatus.FAIL);
  }
}
