package com.f_lab.joyeuse_planete.core.util.web;

public abstract class BeanValidationErrorMessage {

  public static final String DEFAULT_ERROR_MESSAGE = "요청값에 오류가 있습니다. 요청을 다시 확인해주세요.";
  public static final String NO_NEGATIVE_ERROR_MESSAGE = "0 미만의 값은 입력할 수 없습니다.";
  public static final String NO_ABOVE_MAXIMUM_ERROR_MESSAGE = "999999999를 초과한 값은 입력할 수 없습니다.";
  public static final String NO_FUTURE_DATE_ERROR_MESSAGE = "과거 또는 현재 날짜만을 허용합니다.";
  public static final String NO_VALID_ORDER_STATUS_VALUE_ERROR_MESSAGE = "유효하지 않은 주문 상태 값입니다.";
  public static final String NO_SIMULTANEOUS_PRICE_LOW_AND_HIGH_ERROR_MESSAGE = "높은 가격 순, 낮은 가격 순은 동시에 수행할 수 없는 검색조건입니다.";
  public static final String INVALID_COLLECTION_TIME_ERROR_MESSAGE = "픽업 시작 시간은 픽업 마지막 시간 보다 더 앞에 설정되어야 합니다.";
  public static final String INVALID_DATE_RANGE_ERROR_MESSAGE = "시작 날짜는 마지막 날짜 보다 더 앞에 설정되어야 합니다.";
  public static final String INVALID_COST_RANGE_ERROR_MESSAGE = "최소가격은 최대가격보다 같거나 낮게 설정되어야 합니다.";
  public static final String FOOD_ID_NULL_ERROR_MESSAGE = "음식 ID는 필수 값입니다.";
  public static final String FOOD_NULL_ERROR_MESSAGE = "음식 이름은 필수 값입니다.";
  public static final String STORE_ID_NULL_ERROR_MESSAGE = "가게 ID는 필수 값입니다.";
  public static final String STORE_NAME_NULL_ERROR_MESSAGE = "가게 이름은 필수 값입니다.";
  public static final String CURRENCY_ID_NULL_ERROR_MESSAGE = "화폐 ID는 필수 값입니다.";
  public static final String PRICE_NULL_ERROR_MESSAGE = "가격은 필수 값입니다.";
  public static final String TOTAL_COST_NULL_ERROR_MESSAGE = "총 결제 금액은 필수 값입니다.";
  public static final String QUANTITY_NULL_ERROR_MESSAGE = "수량은 필수 값입니다.";
  public static final String CURRENCY_NULL_ERROR_MESSAGE = "결제 화폐는 필수 값입니다.";
  public static final String COLLECTION_START_TIME_NULL_ERROR_MESSAGE = "픽업 시작 시간은 필수 값입니다.";
  public static final String COLLECTION_END_TIME_NULL_ERROR_MESSAGE = "픽업 마지막 시간은 필수 값입니다.";
  public static final String NICKNAME_INVALID_PATTERN = "올바른 닉네임 형식이 아닙니다.";
  public static final String NICKNAME_NOT_BLANK_PATTERN = "닉네임은 필수 값입니다.";
  public static final String EMAIL_INVALID_PATTERN = "올바른 이메일 형식이 아닙니다.";
  public static final String EMAIL_NOT_BLANK_PATTERN = "이메일은 필수 값입니다.";
  public static final String PASSWORD_INVALID_PATTERN = "올바른 비밀번호 형식이 아닙니다.";
  public static final String PASSWORD_NOT_BLANK_PATTERN = "비밀번호은 필수 값입니다.";
}
