package com.f_lab.joyeuse_planete.orders.controller;



import com.f_lab.joyeuse_planete.core.util.web.CommonResponses;
import com.f_lab.joyeuse_planete.orders.dto.request.OrderCreateRequestDTO;
import com.f_lab.joyeuse_planete.orders.dto.request.OrderSearchCondition;
import com.f_lab.joyeuse_planete.orders.dto.response.OrderDTO;
import com.f_lab.joyeuse_planete.orders.dto.response.OrderPollingResponseDTO;
import com.f_lab.joyeuse_planete.orders.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;



@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController {

  private final OrderService orderService;

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public Page<OrderDTO> getOrderList(@ModelAttribute OrderSearchCondition condition) {
    PageRequest pageRequest = PageRequest.of(condition.getPage(), condition.getSize());

    return orderService.getOrderList(condition, pageRequest);
  }

  @GetMapping("/{orderId}")
  public ResponseEntity<OrderDTO> getOrder(@PathVariable("orderId") Long orderId) {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(orderService.getOrder(orderId));
  }

  @PostMapping("/foods")
  public ResponseEntity<String> createFoodOrder(@RequestBody OrderCreateRequestDTO orderCreateRequestDTO) {
    orderService.createFoodOrder(orderCreateRequestDTO);

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(CommonResponses.CREATE_SUCCESS);
  }

  @DeleteMapping("/member/{orderId}")
  public ResponseEntity<String> deleteMemberOrder(@PathVariable("orderId") Long orderId) {
    orderService.deleteOrderByMemberId(orderId);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(CommonResponses.DELETE_SUCCESS);
  }

  @GetMapping("{orderId}/polling/order-status")
  public ResponseEntity<OrderPollingResponseDTO> polling(@PathVariable Long orderId) {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(orderService.polling(orderId));
  }

  @GetMapping("/ping")
  public ResponseEntity<String> healthcheck() {
    return ResponseEntity.status(HttpStatus.OK)
        .body(CommonResponses.PONG);
  }
}
