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
  private transient Long orderId;

  public static FoodReleaseEvent toEvent(OrderCancelEvent event) {
    return FoodReleaseEvent.builder()
        .paymentId(event.getPaymentId())
        .orderId(event.getOrderId())
        .build();
  }
}
