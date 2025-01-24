package com.f_lab.joyeuse_planete.orders.controller;


import com.f_lab.joyeuse_planete.orders.dto.request.OrderCreateRequestDTO;
import com.f_lab.joyeuse_planete.orders.dto.response.OrderCreateResponseDTO;
import com.f_lab.joyeuse_planete.orders.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController {

  private final OrderService orderService;

  @PostMapping("/foods")
  public ResponseEntity<OrderCreateResponseDTO> createFoodOrder(
      @RequestBody OrderCreateRequestDTO orderCreateRequestDTO) {

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(orderService.createFoodOrder(orderCreateRequestDTO));
  }
}
