package com.f_lab.joyeuse_planete.orders.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

@Data
@NoArgsConstructor
public class OrderSearchCondition {

  //TODO Java Bean Validation 추가

  private BigDecimal minCost = BigDecimal.ZERO;
  private BigDecimal maxCost;
  private String status;
  private LocalDateTime startDate = LocalDateTime.of(2024, Month.JANUARY, 1, 0, 0);
  private LocalDateTime endDate;
  private List<String> sortBy = List.of("DATE_NEW");
  private int page = 0;
  private int size = 10;
}
