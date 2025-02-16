package com.f_lab.joyeuse_planete.foods.dto.request;

import com.f_lab.joyeuse_planete.core.domain.Food;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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

  @NotNull(message = "음식 이름은 필수 값입니다.")
  @JsonProperty("food_name")
  private String foodName;

  @NotNull(message = "가격은 필수 값입니다.")
  @Min(value = 0, message = "0 미만의 값은 입력할 수 없습니다.")
  @JsonProperty("price")
  private BigDecimal price;

  @NotNull(message = "수량은 필수 값입니다.")
  @Min(value = 0, message = "0 미만의 값은 입력할 수 없습니다.")
  @JsonProperty("total_quantity")
  private int totalQuantity;

  @NotNull(message = "결제 화폐는 필수 값입니다.")
  @JsonProperty("currency_code")
  private String currencyCode;

  @NotNull(message = "픽업 시작 시간은 필수 값입니다.")
  @JsonProperty("collection_start")
  private LocalDateTime collectionStartTime;

  @NotNull(message = "픽업 마지막 시간은 필수 값입니다.")
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
