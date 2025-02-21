package com.f_lab.joyeuse_planete.payment.service.thirdparty.exceptions;

import com.f_lab.joyeuse_planete.core.exceptions.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentNonRetryableException extends Exception {

  private final ErrorCode errorCode;
}
