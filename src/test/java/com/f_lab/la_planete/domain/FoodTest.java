package com.f_lab.la_planete.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FoodTest {

  @Test
  @DisplayName("음식 수량을 줄였을 때 성공")
  void test_minus_food_quantity_success() {
    // given
    Food food = Food.of(BigDecimal.valueOf(1000), 10);

    // when
    food.minusQuantity(5);

    // then
    assertThat(food.getQuantity()).isEqualTo(5);
  }

  @Test
  @DisplayName("음식 수량을 줄였을 때 수량보다 많아서 실패할 때 예외 던짐")
  void test_minus_food_quantity_fail() {
    // given
    Food food = Food.of(BigDecimal.valueOf(1000), 10);

    // then
    assertThatThrownBy(() -> food.minusQuantity(11))
        .isInstanceOf(IllegalStateException.class);
  }
}