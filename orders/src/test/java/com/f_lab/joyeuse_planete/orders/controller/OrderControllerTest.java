package com.f_lab.joyeuse_planete.orders.controller;

import com.f_lab.joyeuse_planete.core.exceptions.ErrorCode;
import com.f_lab.joyeuse_planete.core.exceptions.JoyeusePlaneteApplicationException;
import com.f_lab.joyeuse_planete.core.util.web.BeanValidationErrorMessage;
import com.f_lab.joyeuse_planete.core.util.web.ResultResponse;
import com.f_lab.joyeuse_planete.core.util.web.ResultResponse.CommonErrorResponses;
import com.f_lab.joyeuse_planete.core.util.web.ResultResponse.CommonResponses;
import com.f_lab.joyeuse_planete.orders.dto.request.OrderCreateRequestDTO;
import com.f_lab.joyeuse_planete.orders.dto.request.OrderSearchCondition;
import com.f_lab.joyeuse_planete.orders.dto.response.OrderCreateResponseDTO;
import com.f_lab.joyeuse_planete.orders.dto.response.OrderDTO;
import com.f_lab.joyeuse_planete.orders.dto.response.OrderPollingResponseDTO;
import com.f_lab.joyeuse_planete.orders.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nullable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

  @Autowired
  MockMvc mockMvc;
  @MockitoBean
  OrderService orderService;

  @Autowired
  ObjectMapper objectMapper;
  private static final String ORDERS_URL_PREFIX = "/api/v1/orders";


  @DisplayName("healthcheck 시 정상 작동 확인")
  @Test
  void testHealthCheckSuccess() throws Exception {
    mockMvc.perform(get(ORDERS_URL_PREFIX + "/ping"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value(CommonResponses.PONG));
  }

  @DisplayName("개별 주문 조회시 성공")
  @Test
  void testGetSingleOrderItemSuccess() throws Exception {
    // given
    Long orderId = 1L;
    OrderDTO expected = createOrderDTO(orderId);
    String expectedJson = objectMapper.writeValueAsString(expected);

    // when
    when(orderService.getOrder(orderId)).thenReturn(expected);

    // then
    mockMvc.perform(get(ORDERS_URL_PREFIX + "/{orderId}", orderId))
        .andExpect(status().isOk())
        .andExpect(content().string(expectedJson));
  }

  @DisplayName("조건에 따른 주문 리스트 조회 성공")
  @Test
  void testOrderListSuccess() throws Exception {
    // given
    OrderSearchCondition condition = createDefaultOrderSearchCondition();
    MultiValueMap<String, String> params = createParams(condition);
    Page<OrderDTO> expected = Page.empty();
    String expectedJson = objectMapper.writeValueAsString(expected);

    // when
    when(orderService.getOrderList(any(), any())).thenReturn(expected);

    // then
    mockMvc.perform(get(ORDERS_URL_PREFIX)
            .params(params))
        .andExpect(status().isOk())
        .andExpect(content().string(expectedJson));
  }

  @DisplayName("주문 생성 성공")
  @Test
  void testOrderCreateSuccess() throws Exception {
    // given
    Long orderId = 1L;
    OrderCreateRequestDTO request = createOrderRequestDTO(1L, "Pizza", 101L, "KRW", "₩", BigDecimal.valueOf(25.99), 2, 500L);
    String content = objectMapper.writeValueAsString(request);
    OrderCreateResponseDTO expected = OrderCreateResponseDTO.of(orderId);
    String expectedJson = objectMapper.writeValueAsString(expected);

    // when
    when(orderService.createFoodOrder(any())).thenReturn(expected);

    // then
    mockMvc.perform(post(ORDERS_URL_PREFIX + "/foods")
            .content(content)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.message").value(CommonResponses.CREATE_SUCCESS))
        .andExpect(content().string(expectedJson));
  }

  @DisplayName("회원의 주문 삭제시 성공")
  @Test
  void testOrderDeleteByMemberSuccess() throws Exception {
    // given
    Long orderId = 1L;
    String expectedJson = objectMapper.writeValueAsString(ResultResponse.of(CommonResponses.DELETE_SUCCESS, HttpStatus.OK.value()));

    // when
    doNothing().when(orderService).deleteOrderByMember(anyLong());

    // then
    mockMvc.perform(delete(ORDERS_URL_PREFIX + "/member/{orderId}", orderId))
        .andExpect(status().isOk())
        .andExpect(content().string(expectedJson));
  }

  @DisplayName("주문 상태 점검시 성공")
  @Test
  void testOrderStatusSuccess() throws Exception {
    // given
    Long orderId = 1L;
    OrderPollingResponseDTO expected = createOrderPollingResponseDTO("READY_FOR_PAYMENT");
    String expectedJson = objectMapper.writeValueAsString(expected);

    // when
    when(orderService.polling(anyLong())).thenReturn(expected);

    // then
    mockMvc.perform(get(ORDERS_URL_PREFIX + "/{orderId}/order-status", orderId))
        .andExpect(status().isOk())
        .andExpect(content().string(expectedJson));
  }

  @DisplayName("존재하지 않는 주문을 조회하였을 경우에 에러 메세지 return")
  @Test
  void testGetNotValidOrderFailAndReturnErrorMessage() throws Exception {
    // given
    Long orderId = 1L;
    String expectedJson = objectMapper.writeValueAsString(ResultResponse.of(ErrorCode.ORDER_NOT_EXIST_EXCEPTION.getDescription(), HttpStatus.BAD_REQUEST.value()));

    // when
    when(orderService.getOrder(anyLong())).thenThrow(new JoyeusePlaneteApplicationException(ErrorCode.ORDER_NOT_EXIST_EXCEPTION));

    // then
    mockMvc.perform(get(ORDERS_URL_PREFIX + "/{orderId}", orderId))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(expectedJson));
  }

  @DisplayName("존재하지 않는 경로로 요청시 실패")
  @Test
  void testNotExistPathFailAndReturnErrorMessage() throws Exception {
    // given
    String NON_EXIST_PATH = "/path/NOT_EXIST_PATH";
    ResultResponse expected = ResultResponse.of(CommonErrorResponses.INCORRECT_ADDRESS, HttpStatus.NOT_FOUND.value());
    String expectedJson = objectMapper.writeValueAsString(expected);

    mockMvc.perform(get(ORDERS_URL_PREFIX + NON_EXIST_PATH))
        .andExpect(status().isNotFound())
        .andExpect(content().json(expectedJson));
  }

  @DisplayName("유효하지 않은 조건으로 주문 검색시 실패1 (가격 범위 X)")
  @Test
  void testGetOrderListWithInvalidSearchCostConditionFail() throws Exception {
    // given
    OrderSearchCondition condition = createOrderSearchCondition(
        BigDecimal.valueOf(1000),
        BigDecimal.valueOf(500),
        "IN_PROGRESS",
        LocalDateTime.now().minusDays(1),
        LocalDateTime.now().plusDays(1),
        List.of("DATE_NEW"),
        0,
        20
    );

    MultiValueMap<String, String> params = createParams(condition);

    // then
    mockMvc.perform(get(ORDERS_URL_PREFIX)
            .params(params))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(containsString(BeanValidationErrorMessage.INVALID_COST_RANGE_ERROR_MESSAGE)));
  }

  @DisplayName("유효하지 않은 조건으로 주문 검색시 실패2 (주문 상태 X)")
  @Test
  void testGetOrderListWithInvalidSearchOrderStatusConditionFail() throws Exception {
    // given
    OrderSearchCondition condition = createOrderSearchCondition(
        BigDecimal.valueOf(1000),
        BigDecimal.valueOf(500),
        "INVALID_STATUS",
        LocalDateTime.now().minusDays(1),
        LocalDateTime.now().plusDays(1),
        List.of("DATE_NEW"),
        0,
        20
    );

    MultiValueMap<String, String> params = createParams(condition);

    // then
    mockMvc.perform(get(ORDERS_URL_PREFIX)
            .params(params))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(containsString(BeanValidationErrorMessage.NO_VALID_ORDER_STATUS_VALUE_ERROR_MESSAGE)));
  }

  @DisplayName("유효하지 않은 조건으로 주문 검색시 실패3 (기간 범위 X)")
  @Test
  void testGetOrderListWithInvalidSearchDateRangeConditionFail() throws Exception {
    // given
    OrderSearchCondition condition = createOrderSearchCondition(
        BigDecimal.valueOf(1000),
        BigDecimal.valueOf(500),
        "READY",
        LocalDateTime.now().plusDays(1),
        LocalDateTime.now().minusDays(1),
        List.of("DATE_NEW"),
        0,
        20
    );

    MultiValueMap<String, String> params = createParams(condition);

    // then
    mockMvc.perform(get(ORDERS_URL_PREFIX)
            .params(params))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(containsString(BeanValidationErrorMessage.INVALID_DATE_RANGE_ERROR_MESSAGE)));
  }

  @DisplayName("유효하지 않은 조건으로 주문 검색시 실패4 (음수 X)")
  @Test
  void testGetOrderListWithInvalidSearchNegativeNumberConditionFail() throws Exception {
    // given
    OrderSearchCondition condition = createOrderSearchCondition(
        BigDecimal.valueOf(1000),
        BigDecimal.valueOf(500),
        "READY",
        LocalDateTime.now().minusDays(1),
        LocalDateTime.now().plusDays(1),
        List.of("DATE_NEW"),
        -1,
        -111
    );

    MultiValueMap<String, String> params = createParams(condition);

    // then
    mockMvc.perform(get(ORDERS_URL_PREFIX)
            .params(params))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(containsString(BeanValidationErrorMessage.NO_NEGATIVE_ERROR_MESSAGE)));
  }

  @DisplayName("상호 배타적인 조건 및 유효하지 않은 조건으로 주문 검색시 실패")
  @Test
  void testMultipleFailConditionsFailAndReturnErrorMessage() throws Exception {
    // given
    OrderSearchCondition condition = createOrderSearchCondition(
        BigDecimal.valueOf(1500),
        BigDecimal.valueOf(500),
        "INVALID_STATUS",
        LocalDateTime.now().plusDays(1),
        LocalDateTime.now().minusDays(1),
        List.of("DATE_NEW"),
        -1,
        -11
    );

    MultiValueMap<String, String> params = createParams(condition);

    // then
    mockMvc.perform(get(ORDERS_URL_PREFIX)
            .params(params))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(containsString(BeanValidationErrorMessage.INVALID_COST_RANGE_ERROR_MESSAGE)))
        .andExpect(content().string(containsString(BeanValidationErrorMessage.NO_VALID_ORDER_STATUS_VALUE_ERROR_MESSAGE)))
        .andExpect(content().string(containsString(BeanValidationErrorMessage.INVALID_DATE_RANGE_ERROR_MESSAGE)))
        .andExpect(content().string(containsString(BeanValidationErrorMessage.NO_NEGATIVE_ERROR_MESSAGE)))
        .andExpect(content().string(containsString(BeanValidationErrorMessage.NO_NEGATIVE_ERROR_MESSAGE)));
  }

  @DisplayName("유효하지 않은 조건으로 주문 생성시 실패")
  @Test
  void testCreateOrderWithMissingCurrencyIdFail() throws Exception {
    // given
    OrderCreateRequestDTO request = createOrderRequestDTO(1L, "Pizza", 101L, null, "₩", BigDecimal.valueOf(25.99), 2, 500L);
    String content = objectMapper.writeValueAsString(request);

    // then
    mockMvc.perform(post(ORDERS_URL_PREFIX + "/foods")
            .content(content)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(containsString(BeanValidationErrorMessage.CURRENCY_NULL_ERROR_MESSAGE)));
  }

  @DisplayName("유효하지 않은 조건들로 주문 생성시 실패")
  @Test
  void testCreateOrderWithMissingCurrencyIdAndMissingFoodIdFail() throws Exception {
    // given
    OrderCreateRequestDTO request = createOrderRequestDTO(null, "Pizza", 101L, null, "₩", BigDecimal.valueOf(25.99), 2, 500L);
    String content = objectMapper.writeValueAsString(request);

    // then
    mockMvc.perform(post(ORDERS_URL_PREFIX + "/foods")
        .content(content)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(containsString(BeanValidationErrorMessage.FOOD_ID_NULL_ERROR_MESSAGE)))
        .andExpect(content().string(containsString(BeanValidationErrorMessage.CURRENCY_NULL_ERROR_MESSAGE)));
  }


  private OrderDTO createOrderDTO(Long orderId) {
    return OrderDTO.builder()
        .orderId(orderId)
        .build();
  }

  private MultiValueMap<String, String> createParams(OrderSearchCondition condition) {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

    if (condition.getMinCost() != null) {
      params.add("minCost", condition.getMinCost().toString());
    }

    if (condition.getMaxCost() != null) {
      params.add("maxCost", condition.getMaxCost().toString());
    }

    if (condition.getStatus() != null && !condition.getStatus().isBlank()) {
      params.add("status", condition.getStatus());
    }

    if (condition.getStartDate() != null) {
      params.add("startDate", condition.getStartDate().toString());
    }

    if (condition.getEndDate() != null) {
      params.add("endDate", condition.getEndDate().toString());
    }

    if (condition.getSortBy() != null && !condition.getSortBy().isEmpty()) {
      params.addAll("sortBy", condition.getSortBy());
    }

    params.add("page", String.valueOf(condition.getPage()));
    params.add("size", String.valueOf(condition.getSize()));

    return params;
  }

  private OrderSearchCondition createDefaultOrderSearchCondition() {
    return new OrderSearchCondition();
  }

  private OrderSearchCondition createOrderSearchCondition(
      @Nullable BigDecimal minCost,
      @Nullable BigDecimal maxCost,
      @Nullable String status,
      @Nullable LocalDateTime startDate,
      @Nullable LocalDateTime endDate,
      @Nullable List<String> sortBy,
      int page,
      int size
  ) {
    OrderSearchCondition condition = new OrderSearchCondition();

    condition.setMinCost(minCost != null ? minCost : BigDecimal.ZERO);
    condition.setMaxCost(maxCost);
    condition.setStatus(status);
    condition.setStartDate(startDate != null ? startDate : LocalDateTime.now().minusMonths(1));
    condition.setEndDate(endDate != null ? endDate : LocalDateTime.now());
    condition.setSortBy(sortBy != null ? sortBy : List.of("DATE_NEW"));
    condition.setPage(page);
    condition.setSize(size);

    return condition;
  }

  private OrderCreateRequestDTO createOrderRequestDTO(
      Long foodId,
      String foodName,
      Long storeId,
      String currencyCode,
      String currencySymbol,
      BigDecimal totalCost,
      int quantity,
      Long voucherId
  ) {
    return OrderCreateRequestDTO.builder()
        .foodId(foodId)
        .foodName(foodName)
        .storeId(storeId)
        .storeName("test")
        .currencyCode(currencyCode)
        .currencySymbol(currencySymbol)
        .totalCost(totalCost)
        .collectionStartTime(LocalTime.now())
        .collectionEndTime(LocalTime.now())
        .quantity(quantity)
        .voucherId(voucherId)
        .build();
  }

  private OrderPollingResponseDTO createOrderPollingResponseDTO(String orderStatus) {
    return OrderPollingResponseDTO.builder()
        .orderStatus(orderStatus)
        .build();
  }
}