package com.f_lab.la_planete.orders.dto.request;


import com.f_lab.la_planete.core.commands.FoodReserveCommand;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateRequestDTO {

  @JsonProperty("food_id")
  private Long foodId;

  @JsonProperty("store_id")
  private Long storeId;

  @JsonProperty("total_amount")
  private BigDecimal totalAmount;

  @JsonProperty("quantity")
  private int quantity;

  @JsonProperty("voucher_id")
  private Long voucherId;

  @JsonProperty("payment_information")
  private PaymentInformation paymentInformation;


  public FoodReserveCommand toFoodReserveCommand() {
    return FoodReserveCommand.builder()
        .foodId(foodId)
        .storeId(storeId)
        .quantity(quantity)
        .totalAmount(totalAmount)
        .voucherId(voucherId)
        .paymentInformation(paymentInformation.toFoodReservePaymentInformation())
        .build();
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  static class PaymentInformation {

    @JsonProperty("card_number")
    private String cardNumber;

    @JsonProperty("card_holdername")
    private String cardHolderName;

    @JsonProperty("expiery_date")
    private String expiryDate;

    @JsonProperty("cvc")
    private String cvc;

    public FoodReserveCommand.PaymentInformation toFoodReservePaymentInformation() {
      return FoodReserveCommand.PaymentInformation.builder()
          .cardNumber(cardNumber)
          .cardHolderName(cardHolderName)
          .expiryDate(expiryDate)
          .cvc(cvc)
          .build();
    }
  }
}
