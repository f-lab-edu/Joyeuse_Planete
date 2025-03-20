package com.f_lab.joyeuse_planete.core.exceptions;

import com.f_lab.joyeuse_planete.core.domain.OrderStatus;

import java.util.Map;

public class ErrorCodeOrderStatusTranslator {

  private static final Map<ErrorCode, OrderStatus> ERROR_CODE_MAP = Map.of(
      ErrorCode.FOOD_NOT_ENOUGH_STOCK, OrderStatus.FAIL_FOOD_QUANTITY,
      ErrorCode.FOOD_NOT_EXIST_EXCEPTION, OrderStatus.FAIL_ORDER,
      ErrorCode.ORDER_NOT_EXIST_EXCEPTION, OrderStatus.FAIL_ORDER
  );

  public static OrderStatus translate(ErrorCode errorCode) {
    return ERROR_CODE_MAP.getOrDefault(errorCode, OrderStatus.FAIL_ORDER);
  }
}
