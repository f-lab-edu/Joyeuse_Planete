package com.f_lab.joyeuse_planete.core.events;

import com.f_lab.joyeuse_planete.core.domain.Order;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class OrderCancelEvent {

  private Long orderId;

  private Long foodId;

  private Long paymentId;

  private Long voucherId;

  private int quantity;

  private BigDecimal totalCost;

  public static OrderCancelEvent from(Order order) {
    return OrderCancelEvent.builder()
        .orderId(order.getId())
        .foodId(order.getFood().getId())
        .paymentId(order.getPayment().getId())
        .voucherId(order.getVoucher().getId())
        .totalCost(order.getTotalCost())
        .quantity(order.getQuantity())
        .build();
  }
}
