package com.f_lab.joyeuse_planete.payment.service.thirdparty.toss;

import com.f_lab.joyeuse_planete.payment.service.thirdparty.token.PaymentToken;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static lombok.AccessLevel.PRIVATE;

@Getter
@AllArgsConstructor(access = PRIVATE)
public class TossPaymentToken extends PaymentToken {

  private final String paymentKey;

  private final String orderId;

  private final String amount;

  public static TossPaymentToken createToken(String processor, String paymentKey, String orderId, String amount) {
    TossPaymentToken token = new TossPaymentToken(paymentKey, orderId, amount);
    token.setProcessor(processor);

    return token;
  }
}
