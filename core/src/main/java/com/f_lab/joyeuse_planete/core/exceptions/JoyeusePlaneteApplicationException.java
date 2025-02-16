package com.f_lab.joyeuse_planete.core.exceptions;

import lombok.Getter;

@Getter
public class JoyeusePlaneteApplicationException extends RuntimeException {

  private ErrorCode errorCode;

  public JoyeusePlaneteApplicationException() {
  }

  public JoyeusePlaneteApplicationException(ErrorCode errorCode) {
    super(errorCode.getDescription());
    this.errorCode = errorCode;
  }

  public JoyeusePlaneteApplicationException(ErrorCode errorCode, Throwable cause) {
    super(errorCode.getDescription(), cause);
    this.errorCode = errorCode;
  }

  public JoyeusePlaneteApplicationException(Throwable cause) {
    super(cause);
  }

  public JoyeusePlaneteApplicationException(ErrorCode errorCode, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(errorCode.getDescription(), cause, enableSuppression, writableStackTrace);
    this.errorCode = errorCode;
  }
}
