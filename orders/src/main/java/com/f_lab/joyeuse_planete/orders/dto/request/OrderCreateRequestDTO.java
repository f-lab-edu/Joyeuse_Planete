package com.f_lab.joyeuse_planete.orders.dto.request;


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

  @NotNull(message = BeanValidationErrorMessage.STORE_ID_NULL_ERROR_MESSAGE)
  @JsonProperty("store_id")
  private Long storeId;

  @NotNull(message = BeanValidationErrorMessage.CURRENCY_ID_NULL_ERROR_MESSAGE)
  @JsonProperty("currency_id")
  private Long currencyId;

  @NotNull(message = BeanValidationErrorMessage.TOTAL_COST_NULL_ERROR_MESSAGE)
  @JsonProperty("total_cost")
  private BigDecimal totalCost;

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
}
