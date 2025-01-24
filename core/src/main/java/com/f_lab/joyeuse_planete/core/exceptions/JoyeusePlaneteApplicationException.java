package com.f_lab.joyeuse_planete.core.exceptions;

public class JoyeusePlaneteApplicationException extends RuntimeException {

  public JoyeusePlaneteApplicationException() {
  }

  public JoyeusePlaneteApplicationException(ErrorCode errorCode) {
    super(errorCode.getDescription());
  }

  public JoyeusePlaneteApplicationException(ErrorCode errorCode, Throwable cause) {
    super(errorCode.getDescription(), cause);
  }

  public JoyeusePlaneteApplicationException(Throwable cause) {
    super(cause);
  }

  public JoyeusePlaneteApplicationException(ErrorCode errorCode, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(errorCode.getDescription(), cause, enableSuppression, writableStackTrace);
  }
}
