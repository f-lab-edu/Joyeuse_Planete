package com.f_lab.joyeuse_planete.foods.dto.request;

import com.f_lab.joyeuse_planete.core.domain.Currency;
import com.f_lab.joyeuse_planete.core.domain.Food;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateFoodRequestDTO {

  @JsonProperty("food_name")
  private String foodName;

  @JsonProperty("price")
  private BigDecimal price;

  @JsonProperty("total_quantity")
  private int totalQuantity;

  @JsonProperty("currency_code")
  private String currencyCode;

  @JsonProperty("collection_start")
  private LocalDateTime collectionStartTime;

  @JsonProperty("collection_end")
  private LocalDateTime collectionEndTime;

  public Food toEntity() {
    return Food.builder()
        .foodName(foodName)
        .price(price)
        .totalQuantity(totalQuantity)
        .collectionStartTime(collectionStartTime)
        .collectionEndTime(collectionEndTime)
        .build();
  }
}
