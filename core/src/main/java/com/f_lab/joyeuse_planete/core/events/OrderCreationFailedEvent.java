package com.f_lab.joyeuse_planete.core.events;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreationFailedEvent {

  private Long orderId;

  private Long foodId;

  private Long storeId;

  private int quantity;

  public static OrderCreationFailedEvent toEvent(OrderCreatedEvent orderCreatedEvent) {
    return OrderCreationFailedEvent.builder()
        .orderId(orderCreatedEvent.getOrderId())
        .foodId(orderCreatedEvent.getFoodId())
        .storeId(orderCreatedEvent.getStoreId())
        .quantity(orderCreatedEvent.getQuantity())
        .build();
  }
}
