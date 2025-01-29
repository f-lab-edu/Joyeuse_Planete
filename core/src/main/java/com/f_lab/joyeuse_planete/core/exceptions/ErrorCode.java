package com.f_lab.joyeuse_planete.core.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static lombok.AccessLevel.PRIVATE;

@Getter
@AllArgsConstructor(access = PRIVATE)
public enum ErrorCode {
  FOOD_NOT_EXIST_EXCEPTION("상품이 존재하지 않습니다.", 400),
  FOOD_NOT_ENOUGH_STOCK("상품의 수량이 부족합니다", 409),
  FOOD_QUANTITY_OVERFLOW("상품의 수량이 최대 값을 넘었습니다.", 406),
  ORDER_NOT_EXIST_EXCEPTION("존재하지 않는 주문입니다.", 406),

  // LOCK
  LOCK_ACQUISITION_FAIL_EXCEPTION("현재 너무 많은 요청을 처리하고 있습니다. 다시 시도해주세요 (락)",503),

  // KAFKA
  KAFKA_RETRY_FAIL_EXCEPTION("오류 발생! 잠시 후 다시 시도해주세요.", 503),
  KAFKA_DEAD_LETTER_TOPIC_FAIL_EXCEPTION("데드레터토픽을 다루는데 실패하였습니다.", 500),
  ;

  private String description;
  private int status;
}
