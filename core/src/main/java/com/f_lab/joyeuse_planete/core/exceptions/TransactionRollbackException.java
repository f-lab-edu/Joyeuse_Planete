package com.f_lab.joyeuse_planete.core.exceptions;

public class TransactionRollbackException extends RuntimeException {

  public TransactionRollbackException(Throwable cause) {
    super(cause);
  }
}
