package com.f_lab.joyeuse_planete.foods.controller;

import com.f_lab.joyeuse_planete.core.exceptions.ErrorCode;
import com.f_lab.joyeuse_planete.core.exceptions.JoyeusePlaneteApplicationException;
import com.f_lab.joyeuse_planete.core.util.web.BeanValidationErrorMessage;
import com.f_lab.joyeuse_planete.core.util.web.ResultResponse;
import com.f_lab.joyeuse_planete.core.util.web.ResultResponse.CommonErrorResponses;
import com.f_lab.joyeuse_planete.core.util.web.ResultResponse.CommonResponses;
import com.f_lab.joyeuse_planete.foods.dto.request.CreateFoodRequestDTO;
import com.f_lab.joyeuse_planete.foods.dto.request.FoodSearchCondition;
import com.f_lab.joyeuse_planete.foods.dto.request.UpdateFoodRequestDTO;
import com.f_lab.joyeuse_planete.foods.dto.response.FoodDTO;
import com.f_lab.joyeuse_planete.foods.service.FoodService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nullable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FoodController.class)
@AutoConfigureMockMvc(addFilters = false)
class FoodControllerTest {

  @Autowired
  MockMvc mockMvc;

  @MockitoBean
  FoodService foodService;

  @Autowired
  ObjectMapper objectMapper;

  static final String FOODS_URL_PREFIX = "/api/v1/foods";

  @DisplayName("healthcheck 시 정상 작동 확인")
  @Test
  void testHealthCheckSuccess() throws Exception {
    mockMvc.perform(get(FOODS_URL_PREFIX + "/ping"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value(CommonResponses.PONG));
  }

  @DisplayName("개별 음식 조회 성공")
  @Test
  void testGetSingleFoodItemSuccess() throws Exception {
    // given
    Long foodId = 1L;
    FoodDTO expected = createFoodDTO(foodId);
    String expectedJson = objectMapper.writeValueAsString(expected);

    // when
    when(foodService.getFood(anyLong())).thenReturn(expected);

    // then
    mockMvc.perform(get(FOODS_URL_PREFIX + "/{foodId}", foodId))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJson));
  }

  @DisplayName("조건에 따른 음식 리스트 조회 성공")
  @Test
  void testFoodListSuccess() throws Exception {
    // given
    FoodSearchCondition condition = createDefaultSearchCondition();
    MultiValueMap<String, String> params = createParams(condition);
    Page<FoodDTO> expected = Page.empty();
    String expectedJson = objectMapper.writeValueAsString(expected);

    // when
    when(foodService.getFoodList(any(), any())).thenReturn(expected);

    // then
    mockMvc.perform(get(FOODS_URL_PREFIX)
            .params(params))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJson));
  }

  @DisplayName("음식 생성시 성공")
  @Test
  void testCreateFoodSuccess() throws Exception {
    // given
    CreateFoodRequestDTO request = createFoodRequest("food", BigDecimal.ONE, 1, "GBP", LocalTime.now(), LocalTime.now().plusHours(1));
    String content = objectMapper.writeValueAsString(request);

    // when
    doNothing().when(foodService).createFood(any());

    // then
    mockMvc.perform(post(FOODS_URL_PREFIX)
            .content(content)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.message").value(CommonResponses.CREATE_SUCCESS));
  }

  @DisplayName("음식 업데이트시 성공")
  @Test
  void testUpdateFoodSuccess() throws Exception {
    // given
    Long foodId = 1L;
    UpdateFoodRequestDTO request = createUpdateFoodRequest("food", BigDecimal.ONE, 1, "GBP", LocalTime.now(), LocalTime.now().plusHours(1));
    String content = objectMapper.writeValueAsString(request);

    // when
    when(foodService.updateFood(any(), any())).thenReturn(null);

    // then
    mockMvc.perform(put(FOODS_URL_PREFIX + "/{foodId}", foodId)
            .content(content)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value(CommonResponses.UPDATE_SUCCESS));
  }

  @DisplayName("음식 삭제시 성공")
  @Test
  void testFoodDeleteSuccess() throws Exception {
    // given
    Long foodId = 1L;

    // when
    doNothing().when(foodService).deleteFood(any());

    // then
    mockMvc.perform(delete(FOODS_URL_PREFIX + "/{foodId}", foodId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value(CommonResponses.DELETE_SUCCESS));
  }

  @DisplayName("존재하지 않는 음식을 조회하였을 경우에 에러 메세지 return")
  @Test
  void testGetNotValidFoodFailAndReturnErrorMessage() throws Exception {
    // given
    Long foodId = 1L;
    ResultResponse expected = ResultResponse.of(ErrorCode.FOOD_NOT_EXIST_EXCEPTION.getDescription(), HttpStatus.BAD_REQUEST.value());
    String expectedJson = objectMapper.writeValueAsString(expected);

    // when
    when(foodService.getFood(anyLong())).thenThrow(
        new JoyeusePlaneteApplicationException(ErrorCode.FOOD_NOT_EXIST_EXCEPTION));

    // then
    mockMvc.perform(get(FOODS_URL_PREFIX + "/{foodId}", foodId))
        .andExpect(status().isBadRequest())
        .andExpect(content().json(expectedJson));
  }

  @DisplayName("존재하지 않는 경로로 요청시 실패")
  @Test
  void testNotExistPathFailAndReturnErrorMessage() throws Exception {
    // given
    String NON_EXIST_PATH = "/path/NOT_EXIST_PATH";
    ResultResponse expected = ResultResponse.of(CommonErrorResponses.INCORRECT_ADDRESS, HttpStatus.NOT_FOUND.value());
    String expectedJson = objectMapper.writeValueAsString(expected);

    mockMvc.perform(get(FOODS_URL_PREFIX + NON_EXIST_PATH))
        .andExpect(status().isNotFound())
        .andExpect(content().json(expectedJson));
  }

  @DisplayName("상호 배타적인 조건으로 검색시 실패")
  @Test
  void testMutuallyExclusiveConditionsFailAndReturnErrorMessage() throws Exception {
    // given
    FoodSearchCondition condition = createSearchCondition(1.0, 1.0, "no", List.of("PRICE_HIGH", "PRICE_LOW"), 0, 20);
    MultiValueMap<String, String> params = createParams(condition);
    ResultResponse expected = ResultResponse.of(BeanValidationErrorMessage.NO_SIMULTANEOUS_PRICE_LOW_AND_HIGH_ERROR_MESSAGE, HttpStatus.BAD_REQUEST.value());
    String expectedJson = objectMapper.writeValueAsString(expected);

    // then
    mockMvc.perform(get(FOODS_URL_PREFIX).params(params))
        .andExpect(status().isBadRequest())
        .andExpect(content().json(expectedJson));
  }

  @DisplayName("상호 배타적인 조건 및 유효하지 않은 조건으로 검색시 실패")
  @Test
  void testMultipleFailConditionsFailAndReturnErrorMessage() throws Exception {
    // given
    FoodSearchCondition condition = createSearchCondition(1.0, 1.0, "no", List.of("PRICE_HIGH", "PRICE_LOW"), -1, -1);
    MultiValueMap<String, String> params = createParams(condition);

    // then
    mockMvc.perform(get(FOODS_URL_PREFIX).params(params))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(containsString(BeanValidationErrorMessage.NO_NEGATIVE_ERROR_MESSAGE)))
        .andExpect(content().string(containsString(BeanValidationErrorMessage.NO_SIMULTANEOUS_PRICE_LOW_AND_HIGH_ERROR_MESSAGE)));
  }

  @DisplayName("픽업 시작 타임이 마지막 타임보다 늦을 때 실패")
  @Test
  void testFoodCreateRequestCollectionStartTimeLaterThanCollectionEndTime() throws Exception {
    // given
    CreateFoodRequestDTO request = createFoodRequest("food", BigDecimal.ONE, 1, "GBP", LocalTime.of(11, 10), LocalTime.of(10, 10));
    String content = objectMapper.writeValueAsString(request);

    // then
    mockMvc.perform(post(FOODS_URL_PREFIX)
            .content(content)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value(BeanValidationErrorMessage.INVALID_COLLECTION_TIME_ERROR_MESSAGE));
  }

  private FoodDTO createFoodDTO(Long foodId) {
    return FoodDTO.builder()
        .foodId(foodId)
        .build();
  }

  private MultiValueMap<String, String> createParams(FoodSearchCondition condition) {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

    if (condition.getLat() != null) {
      params.add("lat", String.valueOf(condition.getLat()));
    }

    if (condition.getLon() != null) {
      params.add("lon", String.valueOf(condition.getLon()));
    }

    if (condition.getSearch() != null && !condition.getSearch().isBlank()) {
      params.add("search", condition.getSearch());
    }

    if (condition.getSortBy() != null && !condition.getSortBy().isEmpty()) {
      params.addAll("sortBy", condition.getSortBy());
    }

    params.add("page", String.valueOf(condition.getPage()));
    params.add("size", String.valueOf(condition.getSize()));

    return params;
  }

  private FoodSearchCondition createDefaultSearchCondition() {
    return new FoodSearchCondition();
  }

  private FoodSearchCondition createSearchCondition(
      @Nullable Double lat,
      @Nullable Double lon,
      @Nullable String search,
      @Nullable List<String> sortBy,
      int page,
      int size
  ) {
    FoodSearchCondition condition = new FoodSearchCondition();
    condition.setLat(lat);
    condition.setLon(lon);
    condition.setSearch(search);
    condition.setSortBy(sortBy);
    condition.setPage(page);
    condition.setSize(size);
    return condition;
  }

  public CreateFoodRequestDTO createFoodRequest(
      String foodName,
      BigDecimal price,
      int totalQuantity,
      String currencyCode,
      LocalTime collectionStartTime,
      LocalTime collectionEndTime
  ) {
    return CreateFoodRequestDTO.builder()
        .foodName(foodName)
        .price(price)
        .totalQuantity(totalQuantity)
        .currencyCode(currencyCode)
        .collectionStartTime(collectionStartTime)
        .collectionEndTime(collectionEndTime)
        .build();
  }

  public UpdateFoodRequestDTO createUpdateFoodRequest(
      String foodName,
      BigDecimal price,
      int totalQuantity,
      String currencyCode,
      LocalTime collectionStartTime,
      LocalTime collectionEndTime
  ) {
    return UpdateFoodRequestDTO.builder()
        .foodName(foodName)
        .price(price)
        .totalQuantity(totalQuantity)
        .currencyCode(currencyCode)
        .collectionStartTime(collectionStartTime)
        .collectionEndTime(collectionEndTime)
        .build();
  }
}