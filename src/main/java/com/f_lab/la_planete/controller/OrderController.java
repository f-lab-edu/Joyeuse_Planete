package com.f_lab.la_planete.controller;

import com.f_lab.la_planete.dto.request.OrderCreateRequestDTO;
import com.f_lab.la_planete.dto.response.OrderCreateResponseDTO;
import com.f_lab.la_planete.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
