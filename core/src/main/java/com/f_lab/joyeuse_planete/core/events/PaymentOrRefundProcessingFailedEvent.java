package com.f_lab.joyeuse_planete.core.events;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentOrRefundProcessingFailedEvent {

  private Long orderId;
  private String errorCode;
  private String message;
  private boolean retryable;

  public static PaymentOrRefundProcessingFailedEvent toEvent(Long orderId, String errorCode, String message, boolean retryable) {
    return PaymentOrRefundProcessingFailedEvent.builder()
        .orderId(orderId)
        .errorCode(errorCode)
        .message(message)
        .retryable(retryable)
        .build();
  }
}
