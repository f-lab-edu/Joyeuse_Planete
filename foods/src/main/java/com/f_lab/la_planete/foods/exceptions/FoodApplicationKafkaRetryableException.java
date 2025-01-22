package com.f_lab.la_planete.foods.exceptions;

import com.f_lab.la_planete.core.kafka.exceptions.RetryableException;

public class FoodApplicationKafkaRetryableException extends RetryableException {

  public FoodApplicationKafkaRetryableException() {
  }

  public FoodApplicationKafkaRetryableException(ErrorCode errorCode) {
    super(errorCode.getDescription());
  }

  public FoodApplicationKafkaRetryableException(ErrorCode errorCode, Throwable cause) {
    super(errorCode.getDescription(), cause);
  }

  public FoodApplicationKafkaRetryableException(Throwable cause) {
    super(cause);
  }

  public FoodApplicationKafkaRetryableException(ErrorCode errorCode, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(errorCode.getDescription(), cause, enableSuppression, writableStackTrace);
  }
}
