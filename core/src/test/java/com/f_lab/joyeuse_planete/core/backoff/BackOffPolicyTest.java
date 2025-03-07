package com.f_lab.joyeuse_planete.core.backoff;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class BackOffPolicyTest {


  @DisplayName("FixedBackoff 확인")
  @Test
  void testFixedBackoff() throws InterruptedException {
    // given
    BackOffPolicy backOffPolicy = new FixedBackOff(1000);
    long start = System.currentTimeMillis();

    // when
    backOffPolicy.apply(1);
    long end = System.currentTimeMillis();

    // then
    assertThat(end - start).isBetween(1000L, 1050L);
  }

  @DisplayName("ExponentialBackoff 확인")
  @Test
  void testExponentialBackoff() throws InterruptedException {
    // given
    BackOffPolicy backOffPolicy = new ExponentialBackOff(2, 1000);
    long start1 = System.currentTimeMillis();

    // when
    backOffPolicy.apply(1);
    long end1 = System.currentTimeMillis();


    long start2 = System.currentTimeMillis();
    backOffPolicy.apply(2);

    long end2 = System.currentTimeMillis();

    // then
    assertThat(end1 - start1).isBetween(1400L, 1900L);
    assertThat(end2 - start2).isBetween(2800L, 4000L);
    assertThat(end2 - start2).isGreaterThan(end1 - start1);
  }
}