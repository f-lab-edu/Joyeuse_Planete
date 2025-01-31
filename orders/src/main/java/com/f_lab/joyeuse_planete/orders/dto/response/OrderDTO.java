package com.f_lab.joyeuse_planete.orders.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Getter
@NoArgsConstructor
public class OrderDTO {

  @JsonProperty("order_id")
  private Long orderId;

  @JsonProperty("food_name")
  private String foodName;

  @JsonProperty("total_cost")
  private BigDecimal totalCost;

  @JsonProperty("currency_code")
  private String currencyCode;

  @JsonProperty("currency_symbol")
  private String currencySymbol;

  @JsonProperty("quantity")
  private int quantity;

  @JsonProperty("status")
  private String status;

  @JsonProperty("payment_id")
  private Long payment;

  @JsonProperty("voucher_id")
  private Long voucher;

  @JsonProperty("created_at")
  private LocalDateTime createdAt;

  @JsonProperty("collection_time")
  private LocalDateTime collectionTime;

  @Builder
  @QueryProjection
  public OrderDTO(
      Long orderId,
      String foodName,
      BigDecimal totalCost,
      String currencyCode,
      String currencySymbol,
      int quantity,
      String status,
      Long payment,
      Long voucher,
      LocalDateTime collectionTime,
      LocalDateTime createdAt
  ) {

    this.orderId = orderId;
    this.foodName = foodName;
    this.totalCost = totalCost;
    this.currencyCode = currencyCode;
    this.currencySymbol = currencySymbol;
    this.quantity = quantity;
    this.status = status;
    this.payment = payment;
    this.voucher = voucher;
    this.collectionTime = collectionTime;
    this.createdAt = createdAt;
  }
}
