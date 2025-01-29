package com.f_lab.joyeuse_planete.orders.service;

import com.f_lab.joyeuse_planete.orders.domain.OrderSearchCondition;
import com.f_lab.joyeuse_planete.orders.dto.response.OrderDTO;
import com.f_lab.joyeuse_planete.orders.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

  @InjectMocks
  OrderService orderService;
  @Mock
  OrderRepository orderRepository;

  @Test
  @DisplayName("orderService 가 올바로 orderRepository를 호출하고 Page를 return 하는 것을 확인")
  void testCallingOnOrderRepositoryAndReturnPageOrderDTO() {
    // given
    Page<OrderDTO> expected = Page.empty();
    OrderSearchCondition condition = new OrderSearchCondition();
    Pageable pageable = PageRequest.of(0, 10);

    // when
    when(orderRepository.findOrders(any(), any())).thenReturn(expected);
    Page<OrderDTO> result = orderService.findOrders(condition, pageable);

    // then
    assertThat(result).isEqualTo(expected);
    verify(orderRepository, times(1)).findOrders(condition, pageable);
  }
}