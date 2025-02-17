package com.f_lab.joyeuse_planete.payment.service.thirdparty.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PaymentResponse {

  private String paymentKey;

  private String orderId;

  private BigDecimal totalAmount;
}
