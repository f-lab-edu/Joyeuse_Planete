package com.f_lab.joyeuse_planete.core.events;

import com.f_lab.joyeuse_planete.core.exceptions.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentProcessingFailedEvent {

  private Long orderId;
  private ErrorCode errorCode;
  private boolean retryable;

  public static PaymentProcessingFailedEvent toEvent(Long orderId, ErrorCode errorCode, boolean retryable) {
    return PaymentProcessingFailedEvent.builder()
        .orderId(orderId)
        .errorCode(errorCode)
        .retryable(retryable)
        .build();
  }
}
