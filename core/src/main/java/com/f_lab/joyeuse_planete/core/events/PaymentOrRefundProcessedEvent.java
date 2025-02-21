package com.f_lab.joyeuse_planete.core.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentOrRefundProcessedEvent {

  private Long orderId;

  public static PaymentOrRefundProcessedEvent toEvent(Long orderId) {
    return PaymentOrRefundProcessedEvent.builder()
        .orderId(orderId)
        .build();
  }
}
