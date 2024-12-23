package com.f_lab.la_planete.domain;

import com.f_lab.la_planete.domain.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter @Setter(PRIVATE)
@NoArgsConstructor(access = PROTECTED)
@Table(name = "payments")
public class Payment extends BaseEntity {

  @Id @GeneratedValue(strategy = IDENTITY)
  private Long id;

  private BigDecimal totalCost;

  @Enumerated(EnumType.STRING)
  private PaymentStatus status;

  @OneToOne(mappedBy = "payment", fetch = LAZY)
  private Order order;

  public static Payment of(BigDecimal totalCost, Order order) {
    Payment payment = new Payment();
    payment.setTotalCost(totalCost);
    payment.setOrder(order);
    payment.setStatus(PaymentStatus.READY);
    return payment;
  }
}
