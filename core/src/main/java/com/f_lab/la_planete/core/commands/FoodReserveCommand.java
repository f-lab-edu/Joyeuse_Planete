package com.f_lab.la_planete.core.commands;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoodReserveCommand implements Serializable {

  private Long foodId;

  private Long storeId;

  private int quantity;

  private BigDecimal totalAmount;

  private Long voucherId;

  private PaymentInformation paymentInformation;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class PaymentInformation {
    private String cardNumber;
    private String cardHolderName;
    private String expiryDate;
    private String cvc;
  }
}
