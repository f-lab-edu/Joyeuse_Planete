package com.f_lab.joyeuse_planete.foods.dto.request;

import com.f_lab.joyeuse_planete.core.util.web.BeanValidationErrorMessage;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.AssertTrue;
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
public class UpdateFoodRequestDTO {

  @NotNull(message = BeanValidationErrorMessage.FOOD_NULL_ERROR_MESSAGE)
  @JsonProperty("food_name")
  private String foodName;

  @NotNull(message = BeanValidationErrorMessage.PRICE_NULL_ERROR_MESSAGE)
  @Min(value = 0, message = BeanValidationErrorMessage.NO_NEGATIVE_ERROR_MESSAGE)
  @JsonProperty("price")
  private BigDecimal price;

  @NotNull(message = BeanValidationErrorMessage.QUANTITY_NULL_ERROR_MESSAGE)
  @Min(value = 0, message = BeanValidationErrorMessage.NO_NEGATIVE_ERROR_MESSAGE)
  @JsonProperty("total_quantity")
  private int totalQuantity;

  @NotNull(message = BeanValidationErrorMessage.CURRENCY_NULL_ERROR_MESSAGE)
  @JsonProperty("currency_code")
  private String currencyCode;

  @NotNull(message = BeanValidationErrorMessage.COLLECTION_START_TIME_NULL_ERROR_MESSAGE)
  @JsonProperty("collection_start")
  private LocalTime collectionStartTime;

  @NotNull(message = BeanValidationErrorMessage.COLLECTION_END_TIME_NULL_ERROR_MESSAGE)
  @JsonProperty("collection_end")
  private LocalTime collectionEndTime;

  @AssertTrue(message = BeanValidationErrorMessage.INVALID_COLLECTION_TIME_ERROR_MESSAGE)
  public boolean isCollectionEndTimeLaterThanCollectionStartTime() {
    return collectionEndTime.isAfter(collectionStartTime);
  }
}
