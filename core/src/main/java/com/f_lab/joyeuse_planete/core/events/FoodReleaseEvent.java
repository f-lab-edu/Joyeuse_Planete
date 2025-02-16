package com.f_lab.joyeuse_planete.core.events;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FoodReleaseEvent {

  private Long paymentId;

  public static FoodReleaseEvent toEvent(OrderCancelEvent event) {
    return FoodReleaseEvent.builder()
        .paymentId(event.getPaymentId())
        .build();
  }
}
