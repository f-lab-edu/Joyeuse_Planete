package com.f_lab.joyeuse_planete.core.domain;

import com.f_lab.joyeuse_planete.core.domain.ids.FoodOrderId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(FoodOrderId.class)
@Table(
    name = "food_order_reserves",
    uniqueConstraints = @UniqueConstraint(columnNames = { "foodId", "orderId" })
)
public class FoodOrderReserve {

  @Id
  private Long foodId;

  @Id
  private Long orderId;

  public static FoodOrderReserve from(Long foodId, Long orderId) {
    return FoodOrderReserve.builder().foodId(foodId).orderId(orderId).build();
  }
}
