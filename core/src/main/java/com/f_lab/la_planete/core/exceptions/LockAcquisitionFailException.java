package com.f_lab.la_planete.core.exceptions;

public class LockAcquisitionFailException extends RuntimeException {

  public LockAcquisitionFailException() {
  }

  public LockAcquisitionFailException(String message) {
    super(message);
  }

  public LockAcquisitionFailException(String message, Throwable cause) {
    super(message, cause);
  }

  public LockAcquisitionFailException(Throwable cause) {
    super(cause);
  }

  public LockAcquisitionFailException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
