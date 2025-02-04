package com.f_lab.joyeuse_planete.foods.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateFoodRequestDTO {

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
}
