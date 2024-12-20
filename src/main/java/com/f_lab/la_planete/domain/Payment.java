package com.f_lab.la_planete.domain;

import com.f_lab.la_planete.domain.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "payments")
public class Payment extends BaseEntity {

  @Id @GeneratedValue(strategy = IDENTITY)
  private Long id;

  private BigDecimal amount;

  @Enumerated(EnumType.STRING)
  private PaymentStatus status;

  @OneToOne(mappedBy = "payment", fetch = LAZY)
  private Order order;
}
