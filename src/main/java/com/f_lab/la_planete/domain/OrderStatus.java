package com.f_lab.la_planete.domain;

import lombok.Getter;

@Getter
public enum OrderStatus {

  READY("주문을 생성하면 가지게 되는 초기 상태입니다."),
  IN_PROGRESS("주문이 진행되고 있는 상태입니다."),
  DONE("주문이 완료된 상태입니다."),
  MEMBER_CANCELED("승인된 결제가 사용자에 의해서 취소된 상태입니다."),
  STORE_CANCELED("승인된 결제가 가게에 의해서 취소된 상태입니다."),
  EXPIRED("주문의 유효 시간 5분이 지나 거래가 취소된 상태입니다."),
  ;

  private final String description;

  private OrderStatus(String description) {
    this.description = description;
  }
}
