package com.f_lab.joyeuse_planete.core.events;

import com.f_lab.joyeuse_planete.core.domain.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCancelEvent {

  private Long orderId;

  private Long foodId;

  private Long paymentId;

  private Long voucherId;

  private int quantity;

  private BigDecimal totalCost;

  public static OrderCancelEvent toEvent(Order order) {
    return OrderCancelEvent.builder()
        .orderId(order.getId())
        .foodId(order.getFoodId())
        .paymentId(order.getPayment().getId())
        .voucherId(order.getVoucher().getId())
        .totalCost(order.getTotalCost())
        .quantity(order.getQuantity())
        .build();
  }
}
