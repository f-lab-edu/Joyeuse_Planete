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
  private transient Long foodId;
  private transient int quantity;

  public static FoodReservationProcessedEvent toEvent(OrderCreatedEvent event) {
    return FoodReservationProcessedEvent.builder()
        .orderId(event.getOrderId())
        .foodId(event.getFoodId())
        .quantity(event.getQuantity())
        .build();
  }
}
