package com.f_lab.joyeuse_planete.foods.dto.request;


import com.f_lab.joyeuse_planete.core.util.web.BeanValidationErrorMessage;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;

@Data
@NoArgsConstructor
public class FoodSearchCondition {

  Double lat = London.lat;
  Double lon = London.lon;

  String search;

  @Min(value = 0, message = BeanValidationErrorMessage.NO_NEGATIVE_ERROR_MESSAGE)
  int page = 0;

  @Min(value = 0, message = BeanValidationErrorMessage.NO_NEGATIVE_ERROR_MESSAGE)
  int size = 50;

  List<String> sortBy = List.of("RATE_HIGH");

  /**
   * PRICE_LOW 와 PRICE_HIGH 정렬 조건이 동시에 주어진다면 에러
   */
  @AssertTrue(message = BeanValidationErrorMessage.NO_SIMULTANEOUS_PRICE_LOW_AND_HIGH_ERROR_MESSAGE)
  public boolean isPriceSortingValid() {
    return !(new HashSet<>(sortBy).containsAll(List.of("PRICE_HIGH", "PRICE_LOW")));
  }

  /**
   * 위도 경도 DEFAULT SET TO LONDON
   */
  @JsonIgnoreType
  static class London {
    private static final Double lat = 51.5072;
    private static final Double lon = -0.118092;
  }
}
