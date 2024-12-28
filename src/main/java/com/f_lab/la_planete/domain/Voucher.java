package com.f_lab.la_planete.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "vouchers")
public class Voucher {

  @Id @GeneratedValue(strategy = IDENTITY)
  private Long id;

  private LocalDateTime expiryDate;

  private BigDecimal percentages;

  public BigDecimal apply(BigDecimal totalCost) {
    BigDecimal afterDiscounts = BigDecimal.valueOf(1).subtract(percentages);
    return totalCost.multiply(afterDiscounts);
  }
}
