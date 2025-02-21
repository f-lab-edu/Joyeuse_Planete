package com.f_lab.joyeuse_planete.payment.service.thirdparty.toss;


import com.f_lab.joyeuse_planete.core.exceptions.ErrorCode;
import com.f_lab.joyeuse_planete.payment.service.thirdparty.exceptions.PaymentNonRetryableException;
import com.f_lab.joyeuse_planete.payment.service.thirdparty.exceptions.PaymentRetryableException;
import com.f_lab.joyeuse_planete.payment.service.thirdparty.toss.TossPaymentProvider.TossPaymentResponseError;

import java.util.Map;

public class TossExceptionTranslator {

  public static final Exception RETRYABLE = new PaymentRetryableException(ErrorCode.PAYMENT_UNKNOWN_EXCEPTION);
  private static final Map<String, Exception> exceptionMap = Map.of(
      "INVALID_STOPPED_CARD", new PaymentNonRetryableException(ErrorCode.INVALID_REJECT_CARD),
      "INVALID_REJECT_CARD", new PaymentNonRetryableException(ErrorCode.PAYMENT_INVALID_STOPPED_CARD_EXCEPTION),
      "EXCEED_MAX_AUTH_COUNT", new PaymentNonRetryableException(ErrorCode.EXCEED_MAX_AUTH_COUNT),
      "NOT_FOUND_PAYMENT", new PaymentNonRetryableException(ErrorCode.NOT_FOUND_PAYMENT),
      "EXCEED_MAX_AMOUNT", new PaymentNonRetryableException(ErrorCode.EXCEED_MAX_AMOUNT),
      "UNAUTHORIZED_KEY", new PaymentNonRetryableException(ErrorCode.UNAUTHORIZED_KEY),
      "REJECT_ACCOUNT_PAYMENT", new PaymentRetryableException(ErrorCode.REJECT_ACCOUNT_PAYMENT),
      "INVALID_PASSWORD", new PaymentRetryableException(ErrorCode.INVALID_PASSWORD)
  );

  public static Exception translate(TossPaymentResponseError error) {
    return exceptionMap.getOrDefault(error.getCode(), RETRYABLE);
  }
}
