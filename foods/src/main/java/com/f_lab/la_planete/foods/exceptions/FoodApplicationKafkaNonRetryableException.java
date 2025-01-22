package com.f_lab.la_planete.foods.exceptions;

import com.f_lab.la_planete.core.kafka.exceptions.NonRetryableException;

public class FoodApplicationKafkaNonRetryableException extends NonRetryableException {

  public FoodApplicationKafkaNonRetryableException() {
  }

  public FoodApplicationKafkaNonRetryableException(ErrorCode errorCode) {
    super(errorCode.getDescription());
  }

  public FoodApplicationKafkaNonRetryableException(ErrorCode errorCode, Throwable cause) {
    super(errorCode.getDescription(), cause);
  }

  public FoodApplicationKafkaNonRetryableException(Throwable cause) {
    super(cause);
  }

  public FoodApplicationKafkaNonRetryableException(ErrorCode errorCode, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(errorCode.getDescription(), cause, enableSuppression, writableStackTrace);
  }
}
