package com.f_lab.la_planete.core.kafka.exceptions;

public class RetryableException extends RuntimeException {

  public RetryableException() {
  }

  public RetryableException(String message) {
    super(message);
  }

  public RetryableException(String message, Throwable cause) {
    super(message, cause);
  }

  public RetryableException(Throwable cause) {
    super(cause);
  }

  public RetryableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
