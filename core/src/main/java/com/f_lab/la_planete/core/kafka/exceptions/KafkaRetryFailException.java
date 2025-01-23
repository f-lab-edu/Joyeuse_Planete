package com.f_lab.la_planete.core.kafka.exceptions;

import com.f_lab.la_planete.core.exceptions.ErrorCode;
import com.f_lab.la_planete.core.exceptions.ApplicationException;

public class KafkaRetryFailException extends ApplicationException {

  public KafkaRetryFailException(ErrorCode errorCode) {
    super(errorCode);
  }

  public KafkaRetryFailException(ErrorCode errorCode, Throwable cause) {
    super(errorCode, cause);
  }
}
