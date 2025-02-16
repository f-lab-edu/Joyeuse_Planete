package com.f_lab.joyeuse_planete.orders.controller;


import com.f_lab.joyeuse_planete.orders.dto.request.OrderCreateRequestDTO;
import com.f_lab.joyeuse_planete.orders.dto.request.OrderSearchCondition;
import com.f_lab.joyeuse_planete.orders.dto.response.OrderCreateResponseDTO;
import com.f_lab.joyeuse_planete.orders.dto.response.OrderDTO;
import com.f_lab.joyeuse_planete.orders.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController {

  private final OrderService orderService;

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public Page<OrderDTO> findOrders(@ModelAttribute OrderSearchCondition condition) {
    PageRequest pageRequest = PageRequest.of(condition.getPage(), condition.getSize());

    return orderService.findOrders(condition, pageRequest);
  }

  @PostMapping("/foods")
  public ResponseEntity<OrderCreateResponseDTO> createFoodOrder(
      @RequestBody OrderCreateRequestDTO orderCreateRequestDTO
  ) {

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(orderService.createFoodOrder(orderCreateRequestDTO));
  }
}
