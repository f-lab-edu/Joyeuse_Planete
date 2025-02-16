package com.f_lab.joyeuse_planete.orders.repository;

import com.f_lab.joyeuse_planete.core.domain.Order;
import com.f_lab.joyeuse_planete.orders.dto.request.OrderCreateRequestDTO;
import com.f_lab.joyeuse_planete.orders.dto.request.OrderSearchCondition;
import com.f_lab.joyeuse_planete.orders.dto.response.OrderDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface OrderRepositoryCustom {

//  Page<OrderDTO> findOrders(Long memberId, OrderSearchCondition condition, Pageable pageable);
  Page<OrderDTO> findOrders(OrderSearchCondition condition, Pageable pageable);
  Order saveOrder(OrderCreateRequestDTO request);
}
