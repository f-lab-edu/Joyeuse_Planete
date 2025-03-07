package com.f_lab.joyeuse_planete.foods.dto.response;

import com.f_lab.joyeuse_planete.core.domain.Food;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalTime;


@Data
@NoArgsConstructor
public class FoodDTO implements Serializable {

  @JsonProperty("food_id")
  private Long foodId;

  @JsonProperty("store_id")
  private Long storeId;

  @JsonProperty("food_name")
  private String foodName;

  @JsonProperty("price")
  private BigDecimal price;

  @JsonProperty("total_quantity")
  private int totalQuantity;

  @JsonProperty("currency_code")
  private String currencyCode;

  @JsonProperty("currency_symbol")
  private String currencySymbol;

  @JsonProperty("rate")
  private BigDecimal rate;

  @JsonProperty("collection_start")
  private LocalTime collectionStartTime;

  @JsonProperty("collection_end")
  private LocalTime collectionEndTime;

  @Builder
  @QueryProjection
  public FoodDTO(
      Long foodId,
      Long storeId,
      String foodName,
      BigDecimal price,
      int totalQuantity,
      String currencyCode,
      String currencySymbol,
      BigDecimal rate,
      LocalTime collectionStartTime,
      LocalTime collectionEndTime
  ) {
    this.foodId = foodId;
    this.storeId = storeId;
    this.foodName = foodName;
    this.price = price;
    this.totalQuantity = totalQuantity;
    this.currencyCode = currencyCode;
    this.currencySymbol = currencySymbol;
    this.rate = rate;
    this.collectionStartTime = collectionStartTime;
    this.collectionEndTime = collectionEndTime;
  }

  public static FoodDTO from(Food food) {
    return FoodDTO.builder()
        .foodId(food.getId())
        .storeId(food.getStore().getId())
        .foodName(food.getFoodName())
        .rate(food.getRate())
        .price(food.getPrice())
        .totalQuantity(food.getTotalQuantity())
        .currencyCode(food.getCurrencyCode())
        .currencySymbol(food.getCurrencySymbol())
        .collectionStartTime(food.getCollectionStartTime())
        .collectionEndTime(food.getCollectionEndTime())
        .build();
  }
}
