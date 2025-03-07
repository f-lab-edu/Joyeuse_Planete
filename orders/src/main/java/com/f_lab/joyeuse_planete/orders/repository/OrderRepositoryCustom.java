package com.f_lab.joyeuse_planete.orders.repository;

import com.f_lab.joyeuse_planete.orders.dto.request.OrderSearchCondition;
import com.f_lab.joyeuse_planete.orders.dto.response.OrderDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface OrderRepositoryCustom {

  Page<OrderDTO> findOrders(OrderSearchCondition condition, Pageable pageable);
  OrderDTO getOrder(Long orderId);
}
