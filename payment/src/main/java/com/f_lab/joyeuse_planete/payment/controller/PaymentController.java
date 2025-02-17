package com.f_lab.joyeuse_planete.payment.controller;

import com.f_lab.joyeuse_planete.core.exceptions.ErrorCode;
import com.f_lab.joyeuse_planete.core.exceptions.JoyeusePlaneteApplicationException;
import com.f_lab.joyeuse_planete.core.util.log.LogUtil;
import com.f_lab.joyeuse_planete.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment")
public class PaymentController {

  private final PaymentService paymentService;

  @GetMapping("/toss-success")
  public void tossSuccess(@RequestParam("paymentKey") String paymentKey,
                          @RequestParam("orderId") Long orderId,
                          @RequestParam("amount") BigDecimal amount) {

    paymentService.processPaymentSuccessToss(paymentKey, orderId, amount);
  }

  @GetMapping("/toss-fail")
  public void tossFail(@RequestParam(value = "orderId", required = false) Long orderId,
                       @RequestParam("code") String code,
                       @RequestParam("message") String message) {

    if (orderId == null) {
      LogUtil.exception("PaymentController.tossFail", new JoyeusePlaneteApplicationException(ErrorCode.ORDER_NOT_PROCESSED_EXCEPTION_CUSTOMER));
      return;
    }

    paymentService.processPaymentFailureToss(orderId, code, message);
  }
}
