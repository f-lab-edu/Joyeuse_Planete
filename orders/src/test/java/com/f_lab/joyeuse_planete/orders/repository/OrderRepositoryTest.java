package com.f_lab.joyeuse_planete.orders.repository;

import com.f_lab.joyeuse_planete.core.domain.Order;
import com.f_lab.joyeuse_planete.core.domain.OrderStatus;
import com.f_lab.joyeuse_planete.orders.domain.OrderSearchCondition;
import com.f_lab.joyeuse_planete.orders.dto.response.OrderDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.test.context.EmbeddedKafka;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@EmbeddedKafka
@SpringBootTest
class OrderRepositoryTest {

  @Autowired OrderRepository orderRepository;

  @BeforeEach
  void beforeEach() {
    orderRepository.saveAll(getOrderList());
  }

  @Test
  @DisplayName("AAA")
  void test() {
    // given
    OrderSearchCondition condition = new OrderSearchCondition();
    Pageable pageable = PageRequest.of(condition.getPage(), condition.getSize());

    // when
    Page<OrderDTO> result = orderRepository.findOrders(condition, pageable);

    for (Order o : orderRepository.findAll()) {
      System.out.println("ACtuAL = " + o);

    }
    // then
    System.out.println("RESULT "  + result);
  }

  private List<Order> getOrderList() {
    List<Order> orders = new ArrayList<>();

    for (int i = 1; i <= 20; i++) {
      OrderStatus status = (i % 3 == 0) ? OrderStatus.READY : (i % 2 == 0 ? OrderStatus.IN_PROGRESS : OrderStatus.DONE);

      orders.add(Order.builder()
          .totalCost(BigDecimal.ONE)
          .quantity(i % 5 + 1)
          .status(status)
          .collectionTime(LocalDateTime.now().plusDays(i))
          .build());
    }

    return orders;
  }
}