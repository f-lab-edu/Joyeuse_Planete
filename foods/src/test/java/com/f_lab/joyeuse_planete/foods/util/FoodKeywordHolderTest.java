package com.f_lab.joyeuse_planete.foods.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FoodKeywordHolderTest {

  @DisplayName("단어가 주어졌을 때 키워드를 반환")
  @Test
  void testGivenInputReturnValidKeyword() {
    // given
    String keyword = "양념치킨";
    List<String> expected = List.of("치킨");

    // when
    List<String> result = FoodKeywordHolder.findMatchingKeywords(keyword);

    // then
    assertThat(result).isEqualTo(expected);
  }

  @DisplayName("단어 리스트가 주어졌을 때 키워드 리스트를 반환")
  @Test
  void testGivenInputListReturnValidKeywordList() {
    // given
    List<String> keywords = List.of("양념치킨", "후라이드 치킨", "매운", "맛있는");
    List<String> expected = List.of("치킨");

    // when
    List<String> result = FoodKeywordHolder.findMatchingKeywords(keywords);

    // then
    assertThat(result).isEqualTo(expected);
  }
}