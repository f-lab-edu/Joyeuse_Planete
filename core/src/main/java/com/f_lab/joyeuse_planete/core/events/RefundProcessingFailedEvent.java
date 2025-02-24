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
public class RefundProcessingFailedEvent {

  private Long orderId;
  private ErrorCode errorCode;
  private boolean retryable;

  public static RefundProcessingFailedEvent toEvent(Long orderId, ErrorCode errorCode, boolean retryable) {
    return RefundProcessingFailedEvent.builder()
        .orderId(orderId)
        .errorCode(errorCode)
        .retryable(retryable)
        .build();
  }
}
