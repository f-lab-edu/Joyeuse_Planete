package com.f_lab.joyeuse_planete.core.events;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FoodReservationProcessedEvent {

  private Long orderId;

  public static FoodReservationProcessedEvent toEvent(OrderCreatedEvent event) {
    return FoodReservationProcessedEvent.builder()
        .orderId(event.getOrderId())
        .build();
  }
}
