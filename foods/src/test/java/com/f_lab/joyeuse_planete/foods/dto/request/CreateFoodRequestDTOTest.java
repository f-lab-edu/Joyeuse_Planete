package com.f_lab.joyeuse_planete.foods.dto.request;

import com.f_lab.joyeuse_planete.core.domain.Food;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


class CreateFoodRequestDTOTest {

  @DisplayName("음식명과 가게명을 기반으로 검색 태그 생성")
  @Test
  void testSearchTagsGeneratedFromFoodNameAndStoreName() {
    // given
    String foodName = "치킨";
    String storeName = "BHC";
    List<String> tags = new ArrayList<>();
    String[] expected = {"치킨", "BHC"};

    // when
    CreateFoodRequestDTO request = createFoodRequestDTO(foodName, storeName, tags);
    Food food = request.toEntity();

    // then
    assertThat(food.getSearchTags()).containsExactlyInAnyOrder(expected);
  }

  @DisplayName("음식명, 가게명, 태그를 포함하여 검색 태그 생성")
  @Test
  void testSearchTagsIncludeFoodNameStoreNameAndTags() {
    // given
    String foodName = "양념치킨";
    String storeName = "BHC";
    List<String> tags = List.of("매운", "달콤한");
    String[] expected = {"양념치킨", "BHC", "매운", "달콤한", "치킨"};

    // when
    CreateFoodRequestDTO request = createFoodRequestDTO(foodName, storeName, tags);
    Food food = request.toEntity();

    // then
    assertThat(food.getSearchTags()).containsExactlyInAnyOrder(expected);
  }

  @DisplayName("태그가 없을 경우 음식명과 가게명만 검색 태그로 사용")
  @Test
  void testSearchTagsWithoutAdditionalTags() {
    // given
    String foodName = "김밥";
    String storeName = "김가네";
    List<String> tags = new ArrayList<>();
    String[] expected = { "김밥", "김가네" };

    // when
    CreateFoodRequestDTO request = createFoodRequestDTO(foodName, storeName, tags);
    Food food = request.toEntity();

    // then
    assertThat(food.getSearchTags()).containsExactlyInAnyOrder(expected);
  }

  @DisplayName("음식명과 태그가 키워드 목록에 존재하면 자동으로 추가됨")
  @Test
  void testSearchTagsIncludeMatchingKeywords() {
    // given
    String foodName = "마라탕";
    String storeName = "샤오미";
    List<String> tags = List.of("얼큰한", "사천음식");
    String[] expected = { "마라탕", "샤오미", "얼큰한", "사천음식" };

    CreateFoodRequestDTO request = createFoodRequestDTO(foodName, storeName, tags);

    // when
    Food food = request.toEntity();

    // then
    assertThat(food.getSearchTags()).containsExactlyInAnyOrder(expected);
  }

  @DisplayName("중복된 태그는 하나로 합쳐짐")
  @Test
  void testDuplicateTagsAreMerged() {
    // given
    String foodName = "햄버거";
    String storeName = "맥도날드";
    List<String> tags = List.of("햄버거", "맥도날드", "치즈버거");
    String[] expected = { "햄버거", "맥도날드", "치즈버거", "버거" };
    CreateFoodRequestDTO request = createFoodRequestDTO(foodName, storeName, tags);

    // when
    Food food = request.toEntity();

    // then
    assertThat(food.getSearchTags()).containsExactlyInAnyOrder(expected);
  }

  private CreateFoodRequestDTO createFoodRequestDTO(
      String foodName,
      String storeName,
      List<String> tags
  ) {
    return CreateFoodRequestDTO.builder()
        .foodName(foodName)
        .price(BigDecimal.ONE)
        .totalQuantity(1111)
        .collectionStartTime(LocalTime.now())
        .collectionEndTime(LocalTime.now().plusHours(2))
        .storeName(storeName)
        .currencyCode("KRW")
        .currencySymbol("₩")
        .tags(tags)
        .build();
  }
}