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
  ORDER_CREATION_FAIL_EXCEPTION("주문을 생성하는데 실패하였습니다. 다시 시도해주세요.", 500),
  ORDER_TOTAL_COST_INVALID_EXCEPTION("결제 금액이 올바르지 않습니다. 다시 시도해주세요", 400),
  ORDER_CANCELLATION_NOT_AVAILABLE_EXCEPTION("요청하신 주문 취소가 주문 취소 허용 시간을 넘겨 진행되지 않았습니다.", 401),
  ORDER_CANCELLATION_FAIL_EXCEPTION("요청하신 주문 취소가 진행되지 않았습니다. 다시 시도해주세요", 406),
  CURRENCY_NOT_EXIST_EXCEPTION("존재하지 않는 화폐입니다.", 400),
  VOUCHER_NOT_EXIST_EXCEPTION("존재하지 않는 할인 쿠폰입니다.", 400),
  VOUCHER_EXPIRED_EXCEPTION("이미 만료된 할인 쿠폰입니다.", 400),

  // 결제 관련 오류
  PAYMENT_NOT_EXIST_EXCEPTION("존재하지 않는 결제입니다.", 400),
  PAYMENT_NOT_SUPPORTED("지원하지 않는 결제입니다.", 400),
  PAYMENT_INVALID_STOPPED_CARD_EXCEPTION("정지된 카드 입니다.", 400),
  INVALID_REJECT_CARD("카드 사용이 거절되었습니다. 카드사 문의가 필요합니다.", 400),
  EXCEED_MAX_AUTH_COUNT("최대 인증 횟수를 초과했습니다. 카드사로 문의해주세요.", 403),
  NOT_FOUND_PAYMENT("존재하지 않는 결제 정보 입니다.", 404),
  EXCEED_MAX_AMOUNT("거래금액 한도를 초과했습니다.", 400),
  UNAUTHORIZED_KEY("인증되지 않은 시크릿 키 혹은 클라이언트 키 입니다.", 401),
  REJECT_ACCOUNT_PAYMENT("잔액 부족으로 결제에 실패했습니다.", 403),
  INVALID_PASSWORD("결제 비밀번호가 일치하지 않습니다.", 403),
  PAYMENT_UNKNOWN_EXCEPTION("알 수 없는 오류가 결제 중 발생하였습니다. 다시 시도해주세요.", 400),

  // 기타 오류
  UNKNOWN_EXCEPTION("알 수 없는 오류가 발생하였습니다. 다시 시도해주세요.", 500),

  // LOCK
  LOCK_ACQUISITION_FAIL_EXCEPTION("현재 너무 많은 요청을 처리하고 있습니다. 다시 시도해주세요.",503),

  // KAFKA
  KAFKA_RETRY_NOT_ACCEPTABLE_EXCEPTION("오류 발생! 잠시 후 다시 시도해주세요.", 400),
  KAFKA_RETRY_FAIL_EXCEPTION("오류 발생! 잠시 후 다시 시도해주세요.", 503),
  KAFKA_DEAD_LETTER_TOPIC_FAIL_EXCEPTION("오류 발생! 잠시 후 다시 시도해주세요.", 500),
  KAFKA_UNAVAILABLE_EXCEPTION("오류 발생! 잠시 후 다시 시도해주세요.", 500),
  ;

  private String description;
  private int status;
}
