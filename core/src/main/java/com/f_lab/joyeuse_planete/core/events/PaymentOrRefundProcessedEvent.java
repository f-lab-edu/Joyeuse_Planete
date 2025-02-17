package com.f_lab.joyeuse_planete.core.events;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentOrRefundProcessedEvent {

  private Long orderId;

  public static PaymentOrRefundProcessedEvent toEvent(Long orderId) {
    return PaymentOrRefundProcessedEvent.builder()
        .orderId(orderId)
        .build();
  }
}
