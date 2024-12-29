package com.f_lab.la_planete.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.Month;


class OrderTest {

  Currency KRW = Currency.builder()
      .currencyCode("KRW")
      .currencySymbol("₩")
      .roundingScale(0)
      .roundingMode(RoundingMode.FLOOR)
      .build();

  Food food = Food.builder()
      .price(BigDecimal.valueOf(10000))
      .currency(KRW)
      .totalQuantity(1000)
      .build();

  Voucher voucher = Voucher.builder()
      .discountRate(BigDecimal.valueOf(0.45))
      .expiryDate(LocalDateTime.of(3000, Month.DECEMBER, 12, 23, 59))
      .build();

  @Test
  @DisplayName("할인 쿠폰이 있는 경우에 calculateTotalCost() 메서드")
  void test_totalCost_given_food_and_voucher_success() {
    // given
    BigDecimal totalCost = food.calculateCost(3);
    Order order = Order.builder()
        .food(food)
        .totalCost(totalCost)
        .quantity(3)
        .voucher(voucher)
        .build();

    // when
    BigDecimal calculatedTotalCost = order.calculateTotalCost();

    // then
    Assertions.assertThat(calculatedTotalCost).isEqualTo(BigDecimal.valueOf(16500));
  }

  @Test
  @DisplayName("할인 쿠폰이 없는 경우에 calculateTotalCost() 메서드")
  void test_totalCost_given_food_and_without_voucher_success() {
    // given
    BigDecimal totalCost = food.calculateCost(3);
    Order order = Order.builder()
        .food(food)
        .totalCost(totalCost)
        .quantity(3)
        .build();

    // when
    BigDecimal calculatedTotalCost = order.calculateTotalCost();

    // then
    Assertions.assertThat(calculatedTotalCost).isEqualTo(BigDecimal.valueOf(30000));
  }
}