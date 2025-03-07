package com.f_lab.joyeuse_planete.orders.controller;


import com.f_lab.joyeuse_planete.core.util.web.ResultResponse;
import com.f_lab.joyeuse_planete.core.util.web.ResultResponse.CommonResponses;
import com.f_lab.joyeuse_planete.orders.dto.request.OrderCreateRequestDTO;
import com.f_lab.joyeuse_planete.orders.dto.request.OrderSearchCondition;
import com.f_lab.joyeuse_planete.orders.dto.response.OrderCreateResponseDTO;
import com.f_lab.joyeuse_planete.orders.dto.response.OrderDTO;
import com.f_lab.joyeuse_planete.orders.dto.response.OrderPollingResponseDTO;
import com.f_lab.joyeuse_planete.orders.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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



@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController {

  private final OrderService orderService;

  @GetMapping("/{orderId}")
  public ResponseEntity<OrderDTO> getOrder(@PathVariable("orderId") Long orderId) {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(orderService.getOrder(orderId));
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public Page<OrderDTO> getOrderList(@ModelAttribute @Valid OrderSearchCondition condition) {
    PageRequest pageRequest = PageRequest.of(condition.getPage(), condition.getSize());

    return orderService.getOrderList(condition, pageRequest);
  }

  @PostMapping("/foods")
  public ResponseEntity<OrderCreateResponseDTO> createFoodOrder(@RequestBody @Valid OrderCreateRequestDTO orderCreateRequestDTO) {
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(orderService.createFoodOrder(orderCreateRequestDTO));
  }

  @DeleteMapping("/member/{orderId}")
  public ResponseEntity<ResultResponse> deleteMemberOrder(@PathVariable("orderId") Long orderId) {
    orderService.deleteOrderByMember(orderId);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(ResultResponse.of(CommonResponses.DELETE_SUCCESS, HttpStatus.OK.value()));
  }

  @GetMapping("{orderId}/order-status")
  public ResponseEntity<OrderPollingResponseDTO> orderStatus(@PathVariable Long orderId) {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(orderService.polling(orderId));
  }

  @GetMapping("/ping")
  public ResponseEntity<ResultResponse> healthcheck() {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(ResultResponse.of(CommonResponses.PONG, HttpStatus.OK.value()));
  }
}
