package com.f_lab.la_planete.core.kafka.exceptions;

public class NonRetryableException extends RuntimeException {

  public NonRetryableException() {
  }

  public NonRetryableException(String message) {
    super(message);
  }

  public NonRetryableException(String message, Throwable cause) {
    super(message, cause);
  }

  public NonRetryableException(Throwable cause) {
    super(cause);
  }

  public NonRetryableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
