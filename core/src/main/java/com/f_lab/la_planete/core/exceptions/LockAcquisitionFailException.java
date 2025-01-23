package com.f_lab.la_planete.core.exceptions;


public class LockAcquisitionFailException extends ApplicationException {

  public LockAcquisitionFailException(ErrorCode errorCode) {
    super(errorCode);
  }

  public LockAcquisitionFailException(ErrorCode errorCode, Throwable cause) {
    super(errorCode, cause);
  }
}
