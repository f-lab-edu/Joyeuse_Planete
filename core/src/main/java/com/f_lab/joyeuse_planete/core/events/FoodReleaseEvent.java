package com.f_lab.joyeuse_planete.core.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoodReleaseEvent {

  private Long paymentId;

  public static FoodReleaseEvent toEvent(OrderCancelEvent event) {
    return FoodReleaseEvent.builder()
        .paymentId(event.getPaymentId())
        .build();
  }
}
