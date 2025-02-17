package com.f_lab.joyeuse_planete.payment.service.thirdparty;

import com.f_lab.joyeuse_planete.core.domain.Payment;
import com.f_lab.joyeuse_planete.core.exceptions.ErrorCode;
import com.f_lab.joyeuse_planete.core.exceptions.JoyeusePlaneteApplicationException;
import com.f_lab.joyeuse_planete.payment.service.thirdparty.response.PaymentResponse;
import com.f_lab.joyeuse_planete.payment.service.thirdparty.token.PaymentToken;
import com.f_lab.joyeuse_planete.payment.service.thirdparty.toss.TossPaymentProvider;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentManagerService {

  private List<PaymentProvider> paymentProviders;

  public Mono<PaymentResponse> processPayment(Payment payment) {
    PaymentProvider paymentProvider = checkValidProvider(payment);
    PaymentToken token = paymentProvider.generateToken(payment);

    return paymentProvider.confirmPayment(token);
  }

  public Mono<PaymentResponse> processRefund(Payment payment) {
    PaymentProvider paymentProvider = checkValidProvider(payment);
    PaymentToken token = paymentProvider.generateToken(payment);

    return paymentProvider.cancelPayment(token);
  }

  private PaymentProvider checkValidProvider(Payment payment) {
    for (PaymentProvider paymentProvider : paymentProviders) {
      if (paymentProvider.supports(payment.getProcessor())) {
        return paymentProvider;
      }
    }

    throw new JoyeusePlaneteApplicationException(ErrorCode.PAYMENT_NOT_SUPPORTED);
  }

  @PostConstruct
  private void init() {
    paymentProviders = new ArrayList<>();
    paymentProviders.add(new TossPaymentProvider());
  }
}
