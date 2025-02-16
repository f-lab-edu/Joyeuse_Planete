package com.f_lab.joyeuse_planete.core.domain;

import lombok.Getter;

@Getter
public enum PaymentStatus {

  READY("결제를 생성하면 가지게 되는 초기 상태입니다. 인증 전까지는 READY 상태를 유지합니다."),
  IN_PROGRESS("결제수단 정보와 해당 결제수단의 소유자가 맞는지 인증을 마친 상태입니다. 결제 승인 API를 호출하면 결제가 완료됩니다."),
  DONE("인증된 결제수단으로 요청한 결제가 승인된 상태입니다."),
  CANCELED("승인된 결제가 취소된 상태입니다."),
  ABORTED("결제 승인이 실패한 상태입니다."),
  EXPIRED("결제 유효 시간 5분이 지나 거래가 취소된 상태입니다."),
  ;

  private final String description;

  private PaymentStatus(String description) {
    this.description = description;
  }
}
