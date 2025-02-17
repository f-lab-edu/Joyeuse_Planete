package com.f_lab.joyeuse_planete.payment.service.thirdparty;

import com.f_lab.joyeuse_planete.core.domain.Payment;
import com.f_lab.joyeuse_planete.payment.service.thirdparty.response.PaymentResponse;
import com.f_lab.joyeuse_planete.payment.service.thirdparty.token.PaymentToken;
import reactor.core.publisher.Mono;

public interface PaymentProvider {

  boolean supports(String processor);

  Mono<PaymentResponse> confirmPayment(PaymentToken token);

  Mono<PaymentResponse> cancelPayment(PaymentToken token);

  PaymentToken generateToken(Payment payment);
}
