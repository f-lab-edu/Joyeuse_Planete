package com.f_lab.joyeuse_planete.orders.service;



import com.f_lab.joyeuse_planete.core.domain.Order;
import com.f_lab.joyeuse_planete.core.kafka.service.KafkaService;
import com.f_lab.joyeuse_planete.orders.domain.OrderSearchCondition;
import com.f_lab.joyeuse_planete.orders.dto.response.OrderDTO;
import com.f_lab.joyeuse_planete.orders.dto.request.OrderCreateRequestDTO;
import com.f_lab.joyeuse_planete.orders.dto.response.OrderCreateResponseDTO;
import com.f_lab.joyeuse_planete.orders.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

  private final OrderRepository orderRepository;
  private final KafkaService kafkaService;

  @Value("${orders.events.topic.name}")
  String ORDER_CREATED_EVENT;

  public Page<OrderDTO> findOrders(OrderSearchCondition condition, Pageable pageable) {
    return orderRepository.findOrders(condition, pageable);
  }

  @Transactional
  public OrderCreateResponseDTO createFoodOrder(OrderCreateRequestDTO request) {
    Order order = request.toEntity();
    try {
      orderRepository.save(order);
    } catch (Exception e) {
      log.error("오류가 발생하였습니다. message = {}", e.getMessage(), e);

    }

    sendKafkaOrderCreatedEvent(request, order);

    return new OrderCreateResponseDTO("PROCESSING");
  }


  public void sendKafkaOrderCreatedEvent(OrderCreateRequestDTO request, Order order) {
    try {
      kafkaService.sendKafkaEvent(ORDER_CREATED_EVENT, request.toEvent(order.getId()));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}

