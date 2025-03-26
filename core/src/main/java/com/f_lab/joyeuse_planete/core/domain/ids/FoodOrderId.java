package com.f_lab.joyeuse_planete.core.domain.ids;


import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
public class FoodOrderId implements Serializable {

  @Id
  private Long foodId;

  @Id
  private Long orderId;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    FoodOrderId that = (FoodOrderId) o;
    return Objects.equals(foodId, that.foodId) &&
        Objects.equals(orderId, that.orderId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(foodId, orderId);
  }

  public static FoodOrderId from(Long foodId, Long orderId) {
    return new FoodOrderId(foodId, orderId);
  }
}
