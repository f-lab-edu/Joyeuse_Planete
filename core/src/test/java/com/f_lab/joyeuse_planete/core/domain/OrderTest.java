package com.f_lab.joyeuse_planete.core.domain;

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

  @Test
  @DisplayName("할인 쿠폰이 있는 경우에 calculateTotalCost() 메서드")
  void test_totalCost_given_food_and_voucher_success() {
    // given
    int quantity = 3;
    BigDecimal totalCost = food.calculateCost(quantity);
    Voucher voucher = createVoucher();
    Order order = createOrder(totalCost, quantity, voucher);

    // when
    BigDecimal calculatedTotalCost = order.calculateTotalCost();

    // then
    BigDecimal NUM_16500 = BigDecimal.valueOf(16500);
    Assertions.assertThat(calculatedTotalCost).isEqualTo(NUM_16500);
  }

  @Test
  @DisplayName("할인 쿠폰이 없는 경우에 calculateTotalCost() 메서드")
  void test_totalCost_given_food_and_without_voucher_success() {
    // given
    int quantity = 3;
    BigDecimal totalCost = food.calculateCost(quantity);
    Order order = createOrder(totalCost, quantity, null);

    // when
    BigDecimal calculatedTotalCost = order.calculateTotalCost();

    // then
    BigDecimal NUM_30000 = BigDecimal.valueOf(30000);
    Assertions.assertThat(calculatedTotalCost).isEqualTo(NUM_30000);
  }

  private Order createOrder(BigDecimal totalCost, int quantity, Voucher voucher) {
    return Order.builder()
        .food(food)
        .totalCost(totalCost)
        .quantity(quantity)
        .voucher(voucher)
        .build();
  }

  private Voucher createVoucher() {
    return Voucher.builder()
        .discountRate(BigDecimal.valueOf(0.45))
        .expiryDate(LocalDateTime.of(3000, Month.DECEMBER, 12, 23, 59))
        .build();
  }
}