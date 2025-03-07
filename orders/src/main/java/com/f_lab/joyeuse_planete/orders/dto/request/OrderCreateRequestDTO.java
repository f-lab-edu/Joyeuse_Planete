package com.f_lab.joyeuse_planete.orders.dto.request;


import com.f_lab.joyeuse_planete.core.domain.Order;
import com.f_lab.joyeuse_planete.core.domain.Voucher;
import com.f_lab.joyeuse_planete.core.events.OrderCreatedEvent;
import com.f_lab.joyeuse_planete.core.util.web.BeanValidationErrorMessage;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateRequestDTO {

  @NotNull(message = BeanValidationErrorMessage.FOOD_ID_NULL_ERROR_MESSAGE)
  @JsonProperty("food_id")
  private Long foodId;

  @NotNull(message = BeanValidationErrorMessage.FOOD_NULL_ERROR_MESSAGE)
  @JsonProperty("food_name")
  private String foodName;

  @NotNull(message = BeanValidationErrorMessage.COLLECTION_START_TIME_NULL_ERROR_MESSAGE)
  @JsonProperty("collection_start_time")
  private LocalTime collectionStartTime;

  @NotNull(message = BeanValidationErrorMessage.COLLECTION_END_TIME_NULL_ERROR_MESSAGE)
  @JsonProperty("collection_end_time")
  private LocalTime collectionEndTime;

  @NotNull(message = BeanValidationErrorMessage.STORE_ID_NULL_ERROR_MESSAGE)
  @JsonProperty("store_id")
  private Long storeId;

  @NotNull(message = BeanValidationErrorMessage.STORE_NAME_NULL_ERROR_MESSAGE)
  @JsonProperty("store_name")
  private String storeName;

  @NotNull(message = BeanValidationErrorMessage.TOTAL_COST_NULL_ERROR_MESSAGE)
  @JsonProperty("total_cost")
  private BigDecimal totalCost;

  @NotNull(message = BeanValidationErrorMessage.CURRENCY_NULL_ERROR_MESSAGE)
  @JsonProperty("currency_code")
  private String currencyCode;

  @NotNull(message = BeanValidationErrorMessage.CURRENCY_NULL_ERROR_MESSAGE)
  @JsonProperty("currency_symbol")
  private String currencySymbol;

  @NotNull(message = BeanValidationErrorMessage.QUANTITY_NULL_ERROR_MESSAGE)
  @Min(value = 0, message = BeanValidationErrorMessage.NO_NEGATIVE_ERROR_MESSAGE)
  @JsonProperty("quantity")
  private int quantity;

  @JsonProperty("voucher_id")
  private Long voucherId;

  public OrderCreatedEvent toEvent(Long orderId) {
    return OrderCreatedEvent.builder()
        .orderId(orderId)
        .foodId(foodId)
        .quantity(quantity)
        .build();
  }

  public Order toEntity(Voucher voucher) {
    return Order.builder()
        .foodId(foodId)
        .foodName(foodName)
        .collectionStartTime(collectionStartTime)
        .collectionEndTime(collectionEndTime)
        .storeId(storeId)
        .storeName(storeName)
        .totalCost(totalCost)
        .currencyCode(currencyCode)
        .currencySymbol(currencySymbol)
        .quantity(quantity)
        .voucher(voucher)
        .build();
  }
}
