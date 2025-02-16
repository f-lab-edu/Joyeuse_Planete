package com.f_lab.joyeuse_planete.core.domain;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public enum OrderStatus {

  READY("주문을 생성하면 가지게 되는 초기 상태입니다."),
  IN_PROGRESS("주문이 진행되고 있는 상태입니다."),
  READY_FOR_PAYMENT("결제에 앞서 모든 준비를 끝내놓은 상태입니다."),
  PENDING_PAYMENT("결제가 실패하였습니다. 5분안에 결제를 완료해주시기를 바랍니다."),
  DONE("주문이 완료된 상태입니다."),
  MEMBER_CANCELED("승인된 결제가 사용자에 의해서 취소된 상태입니다."),
  STORE_CANCELED("승인된 결제가 가게에 의해서 취소된 상태입니다."),
  EXPIRED("주문의 유효 시간 5분이 지나 거래가 취소된 상태입니다."),
  FAIL_FOOD_QUANTITY("요청한 상품의 수량이 부족하여 주문이 실패하였습니다."),
  FAIL_FOOD_INVALID_ID("요청한 상품이 존재하지 않아 주문이 실패하였습니다."),
  FAIL("주문이 실패하였습니다.")
  ;

  private final String description;

  OrderStatus(String description) {
    this.description = description;
  }

  public static List<String> getOrderStatusList() {
    return Arrays.stream(OrderStatus.values())
        .map(Enum::toString)
        .collect(Collectors.toList());
  }
}
