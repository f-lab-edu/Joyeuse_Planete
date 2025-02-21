package com.f_lab.joyeuse_planete.core.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoodReservationProcessedEvent {

  private Long orderId;

  public static FoodReservationProcessedEvent toEvent(OrderCreatedEvent event) {
    return FoodReservationProcessedEvent.builder()
        .orderId(event.getOrderId())
        .build();
  }
}
