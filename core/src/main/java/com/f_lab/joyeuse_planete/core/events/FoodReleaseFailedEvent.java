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
public class FoodReleaseFailedEvent {

  private Long orderId;
  private ErrorCode errorCode;

  public static FoodReleaseFailedEvent toEvent(OrderCancelEvent event, ErrorCode errorCode) {
    return FoodReleaseFailedEvent.builder()
        .orderId(event.getOrderId())
        .errorCode(errorCode)
        .build();
  }

  public static FoodReleaseFailedEvent toEvent(FoodReleaseEvent event, ErrorCode errorCode) {
    return FoodReleaseFailedEvent.builder()
        .orderId(event.getOrderId())
        .errorCode(errorCode)
        .build();
  }
}
