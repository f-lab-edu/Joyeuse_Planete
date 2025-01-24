package com.f_lab.joyeuse_planete.core.domain;

import com.f_lab.joyeuse_planete.core.domain.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;


@Entity
@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "foods")
public class Food extends BaseEntity {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  private String foodName;

  private BigDecimal price;

  private int totalQuantity;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "store_id")
  private Store store;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "currency_id")
  private Currency currency;

  public BigDecimal calculateCost(int quantity) {
    return price.multiply(BigDecimal.valueOf(quantity));
  }

  public void minusQuantity(int quantity) {
    if (totalQuantity - quantity < 0)
      throw new IllegalStateException("수량이 부족합니다. 따라서 구매가 진행될 수 없습니다.");

    totalQuantity -= quantity;
  }
}
