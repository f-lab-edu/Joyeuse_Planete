package com.f_lab.joyeuse_planete.core.events;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentProcessedEvent {

  private Long orderId;

  public static PaymentProcessedEvent toEvent(Long orderId) {
    return PaymentProcessedEvent.builder()
        .orderId(orderId)
        .build();
  }
}
