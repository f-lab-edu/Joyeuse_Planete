package com.f_lab.joyeuse_planete.orders.dto.request;


import com.f_lab.joyeuse_planete.core.domain.OrderStatus;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PastOrPresent;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class OrderSearchCondition {

  @Min(value = 0, message = "0 미만의 값은 입력할 수 없습니다.")
  @Max(value = 999999999, message = "999999999를 초과한 값은 입력할 수 없습니다.")
  private BigDecimal minCost = BigDecimal.ZERO;

  @Min(value = 0, message = "0 미만의 값은 입력할 수 없습니다.")
  @Max(value = 999999999, message = "999999999를 초과한 값은 입력할 수 없습니다.")
  private BigDecimal maxCost;

  private String status;

  @PastOrPresent(message = "과거 또는 현재 날짜만을 허용합니다.")
  private LocalDateTime startDate = LocalDateTime.now().minusMonths(1);

  @PastOrPresent(message = "과거 또는 현재 날짜만을 허용합니다.")
  private LocalDateTime endDate = LocalDateTime.now();

  private List<String> sortBy = List.of("DATE_NEW");

  @Min(value = 0, message = "0 미만의 값은 입력할 수 없습니다.")
  private int page = 0;

  @Min(value = 0, message = "0 미만의 값은 입력할 수 없습니다.")
  private int size = 10;

  @AssertTrue(message = "유효하지 않은 상태 값입니다.")
  public boolean isValidStatus() {
    return status == null || OrderStatus.getOrderStatusList().contains(status.toUpperCase());
  }
}
