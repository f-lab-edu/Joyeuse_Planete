package com.f_lab.joyeuse_planete.core.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static lombok.AccessLevel.PRIVATE;

@Getter
@AllArgsConstructor(access = PRIVATE)
public enum ErrorCode {
  FOOD_NOT_EXIST_EXCEPTION("상품이 존재하지 않습니다.", 400),
  FOOD_NOT_ENOUGH_STOCK("상품의 수량이 부족합니다", 409),
  FOOD_QUANTITY_OVERFLOW("상품의 수량이 최대 값을 넘었습니다.", 400),
  ORDER_NOT_EXIST_EXCEPTION("존재하지 않는 주문입니다.", 400),
  ORDER_NOT_PROCESSED_EXCEPTION_CUSTOMER("유저가 결제를 진행하지 않았습니다.", 422),
  ORDER_CANCELLATION_NOT_AVAILABLE_EXCEPTION("요청하신 주문 취소가 주문 취소허용 시간을 넘겨 진행되지 않았습니다.", 401),
  ORDER_CANCELLATION_FAIL_EXCEPTION("요청하신 주문 취소가 진행되지 않았습니다. 다시 시도해주세요", 406),
  CURRENCY_NOT_EXIST_EXCEPTION("존재하지 않는 화폐입니다.", 400),

  // 결제
  PAYMENT_NOT_EXIST_EXCEPTION("존재하지 않는 결제입니다.", 400),
  PAYMENT_NOT_SUPPORTED("지원하지 않는 결제입니다.", 400),
  UNKNOWN_EXCEPTION("알 수 없는 오류가 발생하였습니다. 다시 시도해주세요.", 500),

  // LOCK
  LOCK_ACQUISITION_FAIL_EXCEPTION("현재 너무 많은 요청을 처리하고 있습니다. 다시 시도해주세요.",503),

  // KAFKA
  KAFKA_RETRY_FAIL_EXCEPTION("오류 발생! 잠시 후 다시 시도해주세요.", 503),
  KAFKA_DEAD_LETTER_TOPIC_FAIL_EXCEPTION("오류 발생! 잠시 후 다시 시도해주세요.", 500),
  KAFKA_UNAVAILABLE_EXCEPTION("오류 발생! 잠시 후 다시 시도해주세요.", 500),
  ;

  private String description;
  private int status;
}
