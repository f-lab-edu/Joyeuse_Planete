package com.f_lab.joyeuse_planete.orders.dto.response;


import com.f_lab.joyeuse_planete.core.domain.Order;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderPollingResponseDTO {

  private String orderStatus;

  public static OrderPollingResponseDTO from(Order order) {
    return OrderPollingResponseDTO.builder()
        .orderStatus(order.getStatus().name())
        .build();
  }
}
