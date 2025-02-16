package com.f_lab.joyeuse_planete.orders.dto.request;


import com.f_lab.joyeuse_planete.core.events.OrderCreatedEvent;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateRequestDTO {

  @NotNull
  @JsonProperty("food_id")
  private Long foodId;

  @NotNull
  @JsonProperty("food_name")
  private String foodName;

  @NotNull
  @JsonProperty("store_id")
  private Long storeId;

  @NotNull
  @JsonProperty("currency_id")
  private Long currencyId;

  @NotNull
  @JsonProperty("total_cost")
  private BigDecimal totalCost;

  @NotNull
  @JsonProperty("quantity")
  private int quantity;

  @NotNull
  @JsonProperty("voucher_id")
  private Long voucherId;

  public OrderCreatedEvent toEvent(Long orderId) {
    return OrderCreatedEvent.builder()
        .orderId(orderId)
        .foodId(foodId)
        .foodName(foodName)
        .storeId(storeId)
        .quantity(quantity)
        .totalAmount(totalCost)
        .voucherId(voucherId)
        .build();
  }
}
