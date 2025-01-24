package com.f_lab.joyeuse_planete.core.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VoucherTest {
  Currency GBP = Currency.builder()
      .currencyCode("GBP")
      .currencySymbol("£")
      .roundingScale(2)
      .roundingMode(RoundingMode.FLOOR)
      .build();

  Currency KRW = Currency.builder()
      .currencyCode("KRW")
      .currencySymbol("₩")
      .roundingScale(0)
      .roundingMode(RoundingMode.FLOOR)
      .build();

  @Test
  @DisplayName("0 ~ 100 사이의 값이 들어올 때 정상적으로 할인이 적용된 값을 return")
  void test_voucher_discount_success() {
    // given
    BigDecimal discountRate = BigDecimal.valueOf(0.25);
    Voucher voucher = createVoucher(discountRate);

    // when
    BigDecimal afterDiscountsGBP1 = voucher.apply(BigDecimal.valueOf(100), GBP);
    BigDecimal afterDiscountsGBP2 = voucher.apply(BigDecimal.valueOf(500.4), GBP);
    BigDecimal afterDiscountsKRW3 = voucher.apply(BigDecimal.valueOf(100), KRW);

    // then
    assertThat(afterDiscountsGBP1).isEqualTo(BigDecimal.valueOf(75.00).setScale(2));
    assertThat(afterDiscountsGBP2).isEqualTo(BigDecimal.valueOf(375.30).setScale(2));
    assertThat(afterDiscountsKRW3).isEqualTo(BigDecimal.valueOf(75));
  }

  @Test
  @DisplayName("currency 값에 null 값이 들어갔을 경우에 예외를 던진다.")
  void test_voucher_discount_failure() {
    // given
    BigDecimal discountRate = BigDecimal.valueOf(0.25);
    Voucher voucher = createVoucher(discountRate);

    // when
    assertThatThrownBy(() ->
        voucher.apply(BigDecimal.valueOf(100), null)
    ).isInstanceOf(IllegalStateException.class);
  }

  private Voucher createVoucher(BigDecimal discountRate) {
    return Voucher.builder()
        .discountRate(discountRate)
        .build();
  }
}