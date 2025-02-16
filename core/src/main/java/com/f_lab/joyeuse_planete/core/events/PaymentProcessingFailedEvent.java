package com.f_lab.joyeuse_planete.core.events;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentProcessingFailedEvent {

  private Long orderId;
  private String errorCode;
  private String message;

  public static PaymentProcessingFailedEvent toEvent(Long orderId, String errorCode, String message) {
    return PaymentProcessingFailedEvent.builder()
        .orderId(orderId)
        .errorCode(errorCode)
        .message(message)
        .build();
  }
}
