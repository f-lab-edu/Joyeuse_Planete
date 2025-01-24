package com.f_lab.joyeuse_planete.core.exceptions;

public class ApplicationException extends RuntimeException {

  public ApplicationException() {
  }

  public ApplicationException(ErrorCode errorCode) {
    super(errorCode.getDescription());
  }

  public ApplicationException(ErrorCode errorCode, Throwable cause) {
    super(errorCode.getDescription(), cause);
  }

  public ApplicationException(Throwable cause) {
    super(cause);
  }

  public ApplicationException(ErrorCode errorCode, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(errorCode.getDescription(), cause, enableSuppression, writableStackTrace);
  }
}
