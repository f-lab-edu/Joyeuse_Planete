package com.f_lab.joyeuse_planete.core.domain;

import com.f_lab.joyeuse_planete.core.domain.base.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "vouchers")
public class Voucher extends BaseTimeEntity {

  @Id @GeneratedValue(strategy = IDENTITY)
  private Long id;

  private LocalDateTime expiryDate;

  private BigDecimal discountRate; // 소수의 값으로 표시됩니다 (e.g. 0.45 == 45%)

  public BigDecimal apply(BigDecimal totalCost, Currency currency) {
    if (ObjectUtils.isEmpty(currency))
      throw new IllegalStateException("화페 값이 비어있습니다. 적절히 변경 후 다시 시도해주시길 바랍니다.");

    BigDecimal multiplier = BigDecimal.ONE.subtract(discountRate);
    return totalCost.multiply(multiplier).setScale(currency.getRoundingScale(), currency.getRoundingMode());
  }

  public boolean hasExpired(LocalDateTime now) {
    return now.isBefore(expiryDate);
  }
}
