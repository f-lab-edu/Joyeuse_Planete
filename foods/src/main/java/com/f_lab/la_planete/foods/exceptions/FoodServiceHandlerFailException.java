package com.f_lab.la_planete.foods.exceptions;

import com.f_lab.la_planete.core.kafka.exceptions.RetryableException;

public class FoodServiceHandlerFailException extends RetryableException {
  public FoodServiceHandlerFailException() {
  }

  public FoodServiceHandlerFailException(String message) {
    super(message);
  }

  public FoodServiceHandlerFailException(String message, Throwable cause) {
    super(message, cause);
  }

  public FoodServiceHandlerFailException(Throwable cause) {
    super(cause);
  }

  public FoodServiceHandlerFailException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
