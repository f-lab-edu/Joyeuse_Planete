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
public class FoodReservationFailedEvent {

  private Long orderId;
  private ErrorCode errorCode;

  public static FoodReservationFailedEvent toEvent(OrderCreatedEvent event, ErrorCode errorCode) {
    return FoodReservationFailedEvent.builder()
        .orderId(event.getOrderId())
        .errorCode(errorCode)
        .build();
  }

  public static FoodReservationFailedEvent toEvent(FoodReservationProcessedEvent event, ErrorCode errorCode) {
    return FoodReservationFailedEvent.builder()
        .orderId(event.getOrderId())
        .errorCode(errorCode)
        .build();
  }
}
