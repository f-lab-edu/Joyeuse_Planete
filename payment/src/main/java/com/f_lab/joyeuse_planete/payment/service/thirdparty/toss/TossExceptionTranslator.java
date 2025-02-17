package com.f_lab.joyeuse_planete.payment.service.thirdparty.toss;


import com.f_lab.joyeuse_planete.payment.service.thirdparty.exceptions.PaymentNonRetryableException;
import com.f_lab.joyeuse_planete.payment.service.thirdparty.exceptions.PaymentRetryableException;
import com.f_lab.joyeuse_planete.payment.service.thirdparty.toss.TossPaymentProvider.TossPaymentResponseError;

import java.util.Map;

public class TossExceptionTranslator {

  public static final Exception RETRYABLE = new PaymentRetryableException("500", "알 수 없는 오류가 발생하였습니다.");
  private static final Map<String, Exception> exceptionMap = Map.of(
      "INVALID_STOPPED_CARD", new PaymentNonRetryableException("400", "정지된 카드 입니다."),
      "INVALID_REJECT_CARD", new PaymentNonRetryableException("400", "카드 사용이 거절되었습니다. 카드사 문의가 필요합니다."),
      "EXCEED_MAX_AUTH_COUNT", new PaymentNonRetryableException("403", "최대 인증 횟수를 초과했습니다. 카드사로 문의해주세요."),
      "NOT_FOUND_PAYMENT", new PaymentNonRetryableException("404", "존재하지 않는 결제 정보 입니다."),
      "EXCEED_MAX_AMOUNT", new PaymentNonRetryableException("400", "거래금액 한도를 초과했습니다."),
      "UNAUTHORIZED_KEY", new PaymentNonRetryableException("401", "인증되지 않은 시크릿 키 혹은 클라이언트 키 입니다."),
      "REJECT_ACCOUNT_PAYMENT", new PaymentRetryableException("403", "잔액부족으로 결제에 실패했습니다."),
      "INVALID_PASSWORD", new PaymentRetryableException("403", "결제 비밀번호가 일치하지 않습니다.")
  );

  public static Exception translate(TossPaymentResponseError error) {
    return exceptionMap.getOrDefault(error.getCode(), RETRYABLE);
  }
}
