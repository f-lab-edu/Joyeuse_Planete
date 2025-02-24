package com.f_lab.joyeuse_planete.orders.service;

import com.f_lab.joyeuse_planete.core.domain.Order;
import com.f_lab.joyeuse_planete.core.domain.OrderStatus;
import com.f_lab.joyeuse_planete.core.events.OrderCancelEvent;
import com.f_lab.joyeuse_planete.core.exceptions.ErrorCode;
import com.f_lab.joyeuse_planete.core.exceptions.JoyeusePlaneteApplicationException;
import com.f_lab.joyeuse_planete.core.util.log.LogUtil;
import com.f_lab.joyeuse_planete.orders.dto.request.OrderCreateRequestDTO;
import com.f_lab.joyeuse_planete.orders.dto.request.OrderSearchCondition;
import com.f_lab.joyeuse_planete.orders.dto.response.OrderCreateResponseDTO;
import com.f_lab.joyeuse_planete.orders.dto.response.OrderDTO;
import com.f_lab.joyeuse_planete.orders.dto.response.OrderPollingResponseDTO;
import com.f_lab.joyeuse_planete.orders.repository.OrderRepository;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Timed("orders")
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

  private final OrderRepository orderRepository;
  private final ApplicationEventPublisher eventPublisher;

  public Page<OrderDTO> getOrderList(OrderSearchCondition condition, Pageable pageable) {
    return orderRepository.findOrders(condition, pageable);
  }

  public OrderDTO getOrder(Long orderId) {
    OrderDTO orderDTO = orderRepository.getOrder(orderId);

    if (orderDTO == null)
      throw new JoyeusePlaneteApplicationException(ErrorCode.ORDER_NOT_EXIST_EXCEPTION);

    return orderDTO;
  }

  @Transactional
  public OrderCreateResponseDTO createFoodOrder(OrderCreateRequestDTO request) {
    Order order;
    try {
      order = orderRepository.saveOrder(request);

    } catch (JoyeusePlaneteApplicationException e) {
      LogUtil.exception("OrderService.createFoodOrder", e);
      throw e;

    } catch (Exception e) {
      LogUtil.exception("OrderService.createFoodOrder", e);
      throw new JoyeusePlaneteApplicationException(ErrorCode.ORDER_CREATION_FAIL_EXCEPTION);
    }

    eventPublisher.publishEvent(request.toEvent(order.getId()));

    return OrderCreateResponseDTO.of(order.getId());
  }

  @Transactional
  public void deleteOrderByMember(Long orderId) {
    Order order;
    try {
      order = findOrderById(orderId);

      if (!order.checkCancellation())
        throw new JoyeusePlaneteApplicationException(ErrorCode.ORDER_CANCELLATION_NOT_AVAILABLE_EXCEPTION);

      updateOrderStatus(orderId, OrderStatus.MEMBER_CANCELED);
      order.setDeleted(true);
      orderRepository.save(order);

    } catch (JoyeusePlaneteApplicationException e) {
      LogUtil.exception("OrderService.deleteOrderByMember", e);
      throw e;

    } catch (Exception e) {
      LogUtil.exception("OrderService.deleteOrderByMember", e);
      throw new JoyeusePlaneteApplicationException(ErrorCode.ORDER_CANCELLATION_FAIL_EXCEPTION);
    }

    eventPublisher.publishEvent(OrderCancelEvent.toEvent(order));
  }

  @Transactional
  public void deleteOrderByStore(Long orderId) {
    // TODO 가게에서 주문 취소 로직
  }

  @Transactional
  public void updateOrderStatus(Long orderId, OrderStatus status) {
    Order order = findOrderById(orderId);
    order.setStatus(status);
    orderRepository.save(order);
  }

  public OrderPollingResponseDTO polling(Long orderId) {
    return OrderPollingResponseDTO.from(findOrderById(orderId));
  }

  private Order findOrderById(Long orderId) {
    return orderRepository.findById(orderId)
        .orElseThrow(() -> new JoyeusePlaneteApplicationException(ErrorCode.ORDER_NOT_EXIST_EXCEPTION));
  }
}

