package com.f_lab.joyeuse_planete.payment.service.thirdparty.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentRetryableException extends Exception {

  private String code;
  private String description;
}
