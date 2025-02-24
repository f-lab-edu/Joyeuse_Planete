package com.f_lab.joyeuse_planete.orders.dto.response;


import com.f_lab.joyeuse_planete.core.domain.Order;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderPollingResponseDTO {

  @JsonProperty("order_status")
  private String orderStatus;

  public static OrderPollingResponseDTO from(Order order) {
    return OrderPollingResponseDTO.builder()
        .orderStatus(order.getStatus().toString())
        .build();
  }
}
