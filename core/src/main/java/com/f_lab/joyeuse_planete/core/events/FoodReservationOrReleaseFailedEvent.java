package com.f_lab.joyeuse_planete.core.events;

import com.f_lab.joyeuse_planete.core.exceptions.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoodReservationOrReleaseFailedEvent {

  private Long orderId;
  private ErrorCode errorCode;

  public static FoodReservationOrReleaseFailedEvent toEvent(OrderCreatedEvent event, ErrorCode errorCode) {
    return FoodReservationOrReleaseFailedEvent.builder()
        .orderId(event.getOrderId())
        .errorCode(errorCode)
        .build();
  }

  public static FoodReservationOrReleaseFailedEvent toEvent(OrderCancelEvent event, ErrorCode errorCode) {
    return FoodReservationOrReleaseFailedEvent.builder()
        .orderId(event.getOrderId())
        .errorCode(errorCode)
        .build();
  }
}
