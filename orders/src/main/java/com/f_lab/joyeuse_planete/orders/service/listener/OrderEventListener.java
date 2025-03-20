package com.f_lab.joyeuse_planete.orders.service.listener;


import com.f_lab.joyeuse_planete.core.domain.OrderStatus;
import com.f_lab.joyeuse_planete.core.events.OrderCancelEvent;
import com.f_lab.joyeuse_planete.core.events.OrderCreatedEvent;
import com.f_lab.joyeuse_planete.core.kafka.service.KafkaService;
import com.f_lab.joyeuse_planete.core.util.log.LogUtil;
import com.f_lab.joyeuse_planete.orders.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@Component
@RequiredArgsConstructor
public class OrderEventListener {

  private final KafkaService kafkaService;
  private final OrderService orderService;

  @Value("${orders.events.topics.create}")
  String ORDER_CREATED_EVENT;

  @Value("${orders.events.topics.cancel}")
  String ORDER_CANCELLATION_EVENT;

  @Async
  @TransactionalEventListener(phase = AFTER_COMMIT)
  public void on(OrderCreatedEvent event) {
    try {
      kafkaService.sendKafkaEvent(ORDER_CREATED_EVENT, event);
    } catch (Exception e) {
      LogUtil.exception("OrderEventListener.on (OrderCreatedEvent)", e);
      orderService.updateOrderStatus(event.getOrderId(), OrderStatus.FAIL_ORDER);
    }
  }

  @Async
  @TransactionalEventListener(phase = AFTER_COMMIT)
  public void on(OrderCancelEvent event) {
    try {
      kafkaService.sendKafkaEvent(ORDER_CANCELLATION_EVENT, event);
    } catch (Exception e) {
      LogUtil.exception("OrderEventListener.on (OrderCancelEvent)", e);
      orderService.updateOrderStatus(event.getOrderId(), OrderStatus.FAIL_CANCEL);
    }
  }
}
