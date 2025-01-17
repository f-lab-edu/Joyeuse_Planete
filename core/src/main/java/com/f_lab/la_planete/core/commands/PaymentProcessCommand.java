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
public class PaymentProcessCommand implements Serializable {

  private Long foodId;

  private String foodName;

  private Long storeId;

  private int quantity;

  private BigDecimal totalAmount;

  private Long voucherId;
//
//  private PaymentInformation paymentInformation;


  public static PaymentProcessCommand toCommand(FoodReserveCommand foodReserveCommand) {
    return PaymentProcessCommand.builder()
        .foodId(foodReserveCommand.getFoodId())
        .quantity(foodReserveCommand.getQuantity())
        .totalAmount(foodReserveCommand.getTotalAmount())
        .build();
  }

}
