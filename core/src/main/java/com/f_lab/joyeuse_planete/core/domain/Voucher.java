package com.f_lab.joyeuse_planete.core.domain;

import com.f_lab.joyeuse_planete.core.domain.base.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Builder
@DynamicInsert
@DynamicUpdate
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@SQLRestriction(value = "expiry_date < NOW() AND is_used IS FALSE")
@Table(name = "vouchers")
public class Voucher extends BaseTimeEntity {

  @Id @GeneratedValue(strategy = IDENTITY)
  private Long id;

  private LocalDateTime expiryDate;

  @Column(columnDefinition = "BOOLEAN NOT NULL DEFAULT 'FALSE'")
  private boolean isUsed;

  private BigDecimal discountRate; // 소수의 값으로 표시됩니다 (e.g. 0.45 == 45%)

  public BigDecimal apply(BigDecimal totalCost, int scale, RoundingMode roundingMode) {
    BigDecimal multiplier = BigDecimal.ONE.subtract(discountRate);
    return totalCost.multiply(multiplier).setScale(scale, roundingMode);
  }

  public boolean hasBeenUsed() {
    return isUsed;
  }

  public void updateToUsed() {
    this.isUsed = true;
  }

  public boolean hasExpired() {
    return LocalDateTime.now().isAfter(expiryDate);
  }
}
