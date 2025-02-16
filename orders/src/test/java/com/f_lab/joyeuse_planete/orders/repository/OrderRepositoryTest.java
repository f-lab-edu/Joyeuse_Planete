package com.f_lab.joyeuse_planete.orders.repository;


import com.f_lab.joyeuse_planete.core.domain.Order;
import com.f_lab.joyeuse_planete.core.domain.OrderStatus;
import com.f_lab.joyeuse_planete.orders.dto.request.OrderSearchCondition;
import com.f_lab.joyeuse_planete.orders.dto.response.OrderDTO;
import jakarta.annotation.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@EmbeddedKafka
@SpringBootTest
class OrderRepositoryTest {

  @Autowired
  OrderRepository orderRepository;

  @BeforeEach
  void beforeEach() {
    orderRepository.saveAll(getOrderList());
  }

  @AfterEach
  void afterEach() {
    orderRepository.deleteAll();
  }

  @Test
  @DisplayName("존재하지 않는 orderId로 조회시 null 반환")
  void testNotValidOrderIdSuccess() {
    // given
    Long orderId = (long) (getOrderList().size() + 2);

    // when
    OrderDTO result = orderRepository.getOrder(orderId);

    // then
    assertThat(result).isNull();
  }

  @Test
  @DisplayName("기본 조건으로 검색했을 때 성공하는 것을 확인")
  void testDefaultConditionSuccess() {
    // given
    OrderSearchCondition condition = creatDefaultSearchCondition();
    Pageable pageable = createPageable(condition);
    List<OrderDTO> expected = getOrderList().stream()
        .filter(o -> o.getCreatedAt().isAfter(LocalDateTime.now().minusMonths(3)))
        .sorted(Comparator.comparing(Order::getCreatedAt).reversed())
        .limit(10)
        .map(this::from)
        .toList();

    // when
    Page<OrderDTO> result = orderRepository.findOrders(condition, pageable);

    // then
    assertTrue(result, expected);
  }

  @Test
  @DisplayName("기본 조건 + 가격 정렬 조건 검색했을 때 성공하는 것을 확인")
  void testDefaultConditionWithMultipleSortBySuccess() {
    // given
    List<String> sortBy = List.of("PRICE_LOW");
    LocalDateTime startDate = LocalDateTime.of(2024, Month.JANUARY, 1, 0, 0);

    OrderSearchCondition condition = createSearchCondition(null, null, null, startDate, null, sortBy, 0, 10);
    Pageable pageable = createPageable(condition);
    List<OrderDTO> expected = getOrderList().stream()
        .filter(o -> o.getCreatedAt().isAfter(startDate.minusDays(1)))
        .sorted(Comparator.comparing(Order::getTotalCost))
        .limit(10)
        .map(this::from)
        .toList();

    // when
    Page<OrderDTO> result = orderRepository.findOrders(condition, pageable);

    // then
    assertTrue(result, expected);
  }

  @Test
  @DisplayName("날짜 조건을 변경하였을 때 작동하는 것을 확인")
  void testDateConditionSuccess() {
    // given
    LocalDateTime startDate = LocalDateTime.of(2024, Month.JANUARY, 1, 0, 0);
    LocalDateTime endDate = LocalDateTime.of(2024, Month.JANUARY, 1, 0, 0);
    List<String> sortBy = List.of("DATE_NEW");

    OrderSearchCondition condition = createSearchCondition(null, null, null, startDate, endDate, sortBy, 0, 10);
    Pageable pageable = createPageable(condition);
    List<OrderDTO> expected = getOrderList().stream()
        .filter(o -> o.getCreatedAt().isAfter(startDate.minusDays(1)))
        .filter(o -> o.getCreatedAt().isBefore(endDate.plusDays(1)))
        .sorted(Comparator.comparing(Order::getCreatedAt).reversed())
        .limit(10)
        .map(this::from)
        .toList();

    // when
    Page<OrderDTO> result = orderRepository.findOrders(condition, pageable);

    // then
    assertTrue(result, expected);
  }

  @Test
  @DisplayName("주문 상태 조건을 변경하였을 때 작동하는 것을 확인")
  void testOrderStatusConditionSuccess() {
    // given
    String status = "FAIL";
    List<String> sortBy = List.of("DATE_NEW");

    OrderSearchCondition condition = createSearchCondition(null, null, status, null, null, sortBy, 0, 10);
    Pageable pageable = createPageable(condition);
    List<OrderDTO> expected = getOrderList().stream()
        .filter(o -> o.getStatus().toString().equals(status))
        .sorted(Comparator.comparing(Order::getCreatedAt).reversed())
        .limit(10)
        .map(this::from)
        .toList();

    // when
    Page<OrderDTO> result = orderRepository.findOrders(condition, pageable);

    // then
    assertTrue(result, expected);
    assertThat(result.getContent().size()).isEqualTo(2);
  }

  @Test
  @DisplayName("가격 상태 조건 + 날짜, 가격 정렬 조건을 변경하였을 때 작동하는 것을 확인")
  void testPriceConditionSuccess() {
    // given
    BigDecimal minCost = BigDecimal.ZERO;
    BigDecimal maxCost = BigDecimal.valueOf(3000.0);
    List<String> sortBy = List.of("PRICE_HIGH", "DATE_OLD");

    OrderSearchCondition condition = createSearchCondition(minCost, maxCost, null, null, null, sortBy, 0, 10);
    Pageable pageable = createPageable(condition);
    List<OrderDTO> expected = getOrderList().stream()
        .filter(o -> o.getTotalCost().compareTo(minCost) >= 0)
        .filter(o -> o.getTotalCost().compareTo(maxCost) <= 0)
        .sorted(Comparator.comparing(Order::getCreatedAt))
        .sorted(Comparator.comparing(Order::getTotalCost).reversed())
        .limit(10)
        .map(this::from)
        .toList();

    // when
    Page<OrderDTO> result = orderRepository.findOrders(condition, pageable);

    // then
    assertTrue(result, expected);
  }

  private void assertTrue(Page<OrderDTO> result, List<OrderDTO> expected) {
    assertThat(result.getContent())
        .usingRecursiveComparison()
        .withComparatorForType(BigDecimal::compareTo, BigDecimal.class)
        .ignoringFields("orderId")
        .isEqualTo(expected);
  }

  @Test
  @DisplayName("페이지 사이즈 조건을 변경하였을 때 작동하는 것을 확인")
  void testPageSizeConditionSuccess() {
    // given
    int size = 20;
    int testDataSize = getOrderList().size();
    int expectedSize = testDataSize / size > 0 ? size : testDataSize % size;
    List<String> sortBy = List.of("DATE_NEW");

    OrderSearchCondition condition = createSearchCondition(null, null, null, null, null, sortBy, 0, size);
    Pageable pageable = createPageable(condition);
    List<OrderDTO> expected = getOrderList().stream()
        .sorted(Comparator.comparing(Order::getCreatedAt).reversed())
        .limit(size)
        .map(this::from)
        .toList();

    // when
    Page<OrderDTO> result = orderRepository.findOrders(condition, pageable);

    // then
    assertTrue(result, expected);
    assertThat(result.getContent().size()).isEqualTo(expectedSize);
  }

  private OrderDTO from(Order order) {
    return OrderDTO.builder()
        .orderId(order.getId())
        .foodName(order.getFood() != null ? order.getFood().getFoodName() : null)
        .totalCost(order.getTotalCost())
        .currencyCode(order.getFood() != null ? order.getFood().getCurrency().getCurrencyCode() : null)
        .currencySymbol(order.getFood() != null ? order.getFood().getCurrency().getCurrencySymbol() : null)
        .quantity(order.getQuantity())
        .status(order.getStatus().name())
        .payment(order.getPayment() != null ? order.getPayment().getId() : null)
        .voucher(order.getVoucher() != null ? order.getVoucher().getId() : null)
        .createdAt(order.getCreatedAt())
        .build();
  }

  private List<Order> getOrderList() {
    return List.of(
        Order.builder()
            .totalCost(BigDecimal.valueOf(1000.0))
            .status(OrderStatus.READY)
            .createdAt(LocalDateTime.of(2024, Month.JANUARY, 21, 10, 10, 10))
            .build(),

        Order.builder()
            .totalCost(BigDecimal.valueOf(2000.0))
            .status(OrderStatus.READY)
            .createdAt(LocalDateTime.of(2024, Month.JANUARY, 21, 10, 10, 10))
            .build(),

        Order.builder()
            .totalCost(BigDecimal.valueOf(2000.0))
            .status(OrderStatus.READY)
            .createdAt(LocalDateTime.of(2023, Month.JANUARY, 21, 10, 10, 10))
            .build(),

        Order.builder()
            .totalCost(BigDecimal.valueOf(3000.0))
            .status(OrderStatus.FAIL)
            .createdAt(LocalDateTime.of(2022, Month.JANUARY, 21, 10, 10, 10))
            .build(),

        Order.builder()
            .totalCost(BigDecimal.valueOf(4000.0))
            .status(OrderStatus.READY)
            .createdAt(LocalDateTime.of(2022, Month.JANUARY, 21, 10, 10, 10))
            .build(),

        Order.builder()
            .totalCost(BigDecimal.valueOf(10000.0))
            .status(OrderStatus.READY)
            .createdAt(LocalDateTime.of(2021, Month.JANUARY, 21, 10, 10, 10))
            .build(),

        Order.builder()
            .totalCost(BigDecimal.valueOf(1000.0))
            .status(OrderStatus.READY)
            .createdAt(LocalDateTime.of(2020, Month.JANUARY, 21, 10, 10, 10))
            .build(),

        Order.builder()
            .totalCost(BigDecimal.valueOf(2000.0))
            .status(OrderStatus.READY)
            .createdAt(LocalDateTime.of(2019, Month.JANUARY, 21, 10, 10, 10))
            .build(),

        Order.builder()
            .totalCost(BigDecimal.valueOf(3000.0))
            .status(OrderStatus.FAIL)
            .createdAt(LocalDateTime.of(2018, Month.JANUARY, 21, 10, 10, 10))
            .build(),

        Order.builder()
            .totalCost(BigDecimal.valueOf(4000.0))
            .status(OrderStatus.READY)
            .createdAt(LocalDateTime.of(2017, Month.JANUARY, 21, 10, 10, 10))
            .build(),

        Order.builder()
            .totalCost(BigDecimal.valueOf(10000.0))
            .status(OrderStatus.READY)
            .createdAt(LocalDateTime.of(2016, Month.JANUARY, 21, 10, 10, 10))
            .build()
    );
  }

  private Pageable createPageable(OrderSearchCondition condition) {
    return PageRequest.of(condition.getPage(), condition.getSize());
  }

  private OrderSearchCondition creatDefaultSearchCondition() {
    return new OrderSearchCondition();
  }

  private OrderSearchCondition createSearchCondition(
      @Nullable BigDecimal minCost,
      @Nullable BigDecimal maxCost,
      @Nullable String status,
      @Nullable LocalDateTime startDate,
      @Nullable LocalDateTime endDate,
      @Nullable List<String> sortBy,
      @Nullable Integer page,
      @Nullable Integer size
  ) {
    OrderSearchCondition condition = new OrderSearchCondition();

    condition.setMinCost(minCost);
    condition.setMaxCost(maxCost);
    condition.setStatus(status);
    condition.setStartDate(startDate);
    condition.setEndDate(endDate);
    condition.setSortBy(sortBy);
    condition.setPage((page == null) ? 0 : page);
    condition.setSize((size == null) ? 0 : size);

    return condition;
  }
}