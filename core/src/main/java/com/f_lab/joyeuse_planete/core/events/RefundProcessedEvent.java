package com.f_lab.joyeuse_planete.core.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundProcessedEvent {

  private Long orderId;

  public static RefundProcessedEvent toEvent(Long orderId) {
    return RefundProcessedEvent.builder()
        .orderId(orderId)
        .build();
  }
}
