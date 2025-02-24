package com.f_lab.joyeuse_planete.core.domain;


import com.f_lab.joyeuse_planete.core.domain.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

import static com.f_lab.joyeuse_planete.core.util.time.TimeConstantsString.THIRTY_MINUTES;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@SuperBuilder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@Table(name = "orders")
public class Order extends BaseEntity {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "food_id")
  private Food food;

  private BigDecimal totalCost;

  private int quantity;

  private double rate;

  @Enumerated(EnumType.STRING)
  private OrderStatus status;

  @OneToOne(fetch = LAZY)
  @JoinColumn(name = "payment_id")
  private Payment payment;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "voucher_id")
  private Voucher voucher;

  public BigDecimal calculateTotalCost() {
    return (voucher != null)
        ? voucher.apply(food.calculateCost(quantity), food.getCurrency())
        : food.calculateCost(quantity);
  }

  public boolean checkCancellation() {
    return food.getCollectionStartTime().isAfter(
        LocalTime.now().plusMinutes(TimeUnit.MILLISECONDS.toMinutes(Long.parseLong(THIRTY_MINUTES))));
  }
}
