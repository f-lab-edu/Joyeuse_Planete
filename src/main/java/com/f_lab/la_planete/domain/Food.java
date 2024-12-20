package com.f_lab.la_planete.domain;

import com.f_lab.la_planete.domain.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Setter(PRIVATE)
@NoArgsConstructor(access = PROTECTED)
@Table(name = "foods")
public class Food extends BaseEntity {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  private BigDecimal price;

  private int quantity;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "store_id")
  private Store store;

  public static Food of(BigDecimal price, int quantity) {
    Food food = new Food();
    food.setPrice(price);
    food.setQuantity(quantity);
    return food;
  }

  public void minusQuantity(int deductions) {
    if (quantity - deductions < 0)
      throw new IllegalStateException("수량이 부족합니다. 따라서 구매가 진행될 수 없습니다.");

    quantity -= deductions;
  }
}
