package com.f_lab.joyeuse_planete.orders.service;


import com.f_lab.joyeuse_planete.core.domain.Order;
import com.f_lab.joyeuse_planete.core.exceptions.JoyeusePlaneteApplicationException;
import com.f_lab.joyeuse_planete.orders.dto.request.OrderCreateRequestDTO;
import com.f_lab.joyeuse_planete.orders.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.test.context.EmbeddedKafka;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@EmbeddedKafka
@SpringBootTest
public class OrderServiceKafkaTest {

  @InjectMocks
  OrderService orderService;
  @Mock
  ApplicationEventPublisher eventPublisher;
  @Mock
  OrderRepository orderRepository;
  @Mock
  VoucherService voucherService;

  @Test
  @DisplayName("주문 생성 메서드 호출 후 성공")
  void testCreateOrderSuccess() {
    // given
    OrderCreateRequestDTO request = createOrderCreateRequestDTO();
    Order order = createOrder();

    // when
    when(orderRepository.save(any())).thenReturn(order);
    when(voucherService.getValidVoucher(any())).thenReturn(null);
    doNothing().when(eventPublisher).publishEvent(any(Object.class));
    orderService.createFoodOrder(request);

    // then
    verify(orderRepository, times(1)).save(any());
  }

  @Test
  @DisplayName("주문 생성 메서드 호출 후 데이터베이스 관련 에러로 인한 실패 1")
  void testCreateOrderRepositoryThrowExceptionFail1() {
    // given
    OrderCreateRequestDTO request = createOrderCreateRequestDTO();

    // when
    when(orderRepository.save(any())).thenThrow(new RuntimeException());

    // then
    assertThatThrownBy(() -> orderService.createFoodOrder(request))
        .isInstanceOf(RuntimeException.class);
  }

  @Test
  @DisplayName("주문 생성 메서드 호출 후 데이터베이스 관련 에러로 인한 실패 2")
  void testCreateOrderRepositoryThrowExceptionFail2() {
    // given
    OrderCreateRequestDTO request = createOrderCreateRequestDTO();

    // when
    when(orderRepository.save(any())).thenThrow(new JoyeusePlaneteApplicationException());

    // then
    assertThatThrownBy(() -> orderService.createFoodOrder(request))
        .isInstanceOf(RuntimeException.class);
  }

  private OrderCreateRequestDTO createOrderCreateRequestDTO() {
    return OrderCreateRequestDTO.builder()
        .foodId(1L)
        .foodName("Test Food")
        .storeId(10L)
        .totalCost(new BigDecimal("19.99"))
        .quantity(2)
        .voucherId(200L)
        .build();
  }

  private Order createOrder() {
    return Order.builder().id(1L).build();
  }
}
