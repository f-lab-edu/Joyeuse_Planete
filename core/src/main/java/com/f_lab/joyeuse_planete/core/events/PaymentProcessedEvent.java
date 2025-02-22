package com.f_lab.joyeuse_planete.core.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentProcessedEvent {

  private Long orderId;

  public static PaymentProcessedEvent toEvent(Long orderId) {
    return PaymentProcessedEvent.builder()
        .orderId(orderId)
        .build();
  }
}
