package com.f_lab.joyeuse_planete.foods.dto.request;

import com.f_lab.joyeuse_planete.core.domain.Food;
import com.f_lab.joyeuse_planete.core.util.web.BeanValidationErrorMessage;
import com.f_lab.joyeuse_planete.foods.util.FoodKeywordHolder;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateFoodRequestDTO {

  @NotNull(message = BeanValidationErrorMessage.FOOD_NULL_ERROR_MESSAGE)
  @JsonProperty("food_name")
  private String foodName;

  @NotNull(message = BeanValidationErrorMessage.STORE_NAME_NULL_ERROR_MESSAGE)
  @JsonProperty("store_name")
  private String storeName;

  @Builder.Default
  private List<String> tags = new ArrayList<>();

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

  @NotNull(message = BeanValidationErrorMessage.CURRENCY_NULL_ERROR_MESSAGE)
  @JsonProperty("currency_symbol")
  private String currencySymbol;

  @NotNull(message = BeanValidationErrorMessage.COLLECTION_START_TIME_NULL_ERROR_MESSAGE)
  @JsonProperty("collection_start")
  private LocalTime collectionStartTime;

  @NotNull(message = BeanValidationErrorMessage.COLLECTION_END_TIME_NULL_ERROR_MESSAGE)
  @JsonProperty("collection_end")
  private LocalTime collectionEndTime;

  @AssertTrue(message = BeanValidationErrorMessage.INVALID_COLLECTION_TIME_ERROR_MESSAGE)
  public boolean isCollectionEndTimeAfterCollectionStartTime() {
    return collectionEndTime.isAfter(collectionStartTime);
  }

  public Food toEntity() {
    return Food.builder()
        .foodName(foodName)
        .price(price)
        .tags(tags)
        .searchTags(generateSearchKeywords())
        .totalQuantity(totalQuantity)
        .collectionStartTime(collectionStartTime)
        .collectionEndTime(collectionEndTime)
        .build();
  }

  private List<String> generateSearchKeywords() {
    Set<String> searchTags = new HashSet<>();

    searchTags.addAll(FoodKeywordHolder.findMatchingKeywords(foodName));
    searchTags.addAll(FoodKeywordHolder.findMatchingKeywords(storeName));
    searchTags.addAll(FoodKeywordHolder.findMatchingKeywords(tags));

    searchTags.add(foodName);
    searchTags.add(storeName);
    searchTags.addAll(tags);

    return new ArrayList<>(searchTags);
  }
}
