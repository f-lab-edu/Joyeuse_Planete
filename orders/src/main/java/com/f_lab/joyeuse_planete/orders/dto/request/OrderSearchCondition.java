package com.f_lab.joyeuse_planete.orders.dto.request;


import com.f_lab.joyeuse_planete.core.domain.OrderStatus;
import com.f_lab.joyeuse_planete.core.util.web.BeanValidationErrorMessage;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderSearchCondition {

  @Min(value = 0, message = BeanValidationErrorMessage.NO_NEGATIVE_ERROR_MESSAGE)
  @Max(value = 999999999, message = BeanValidationErrorMessage.NO_ABOVE_MAXIMUM_ERROR_MESSAGE)
  private BigDecimal minCost = BigDecimal.ZERO;

  @Min(value = 0, message = BeanValidationErrorMessage.NO_NEGATIVE_ERROR_MESSAGE)
  @Max(value = 999999999, message = BeanValidationErrorMessage.NO_ABOVE_MAXIMUM_ERROR_MESSAGE)
  private BigDecimal maxCost;

  private String status;

  @PastOrPresent(message = BeanValidationErrorMessage.NO_FUTURE_DATE_ERROR_MESSAGE)
  private LocalDateTime startDate = LocalDateTime.now().minusMonths(1);

  @PastOrPresent(message = BeanValidationErrorMessage.NO_FUTURE_DATE_ERROR_MESSAGE)
  private LocalDateTime endDate = LocalDateTime.now();

  private List<String> sortBy = List.of("DATE_NEW");

  @Min(value = 0, message = BeanValidationErrorMessage.NO_NEGATIVE_ERROR_MESSAGE)
  private int page = 0;

  @Min(value = 0, message = BeanValidationErrorMessage.NO_NEGATIVE_ERROR_MESSAGE)
  private int size = 10;

  @AssertTrue(message = BeanValidationErrorMessage.NO_VALID_ORDER_STATUS_VALUE_ERROR_MESSAGE)
  public boolean isValidOrderStatus() {
    return status == null || OrderStatus.getOrderStatusList().contains(status.toUpperCase());
  }

  @AssertTrue(message = BeanValidationErrorMessage.INVALID_COST_RANGE_ERROR_MESSAGE)
  public boolean isMinCostLessThanMaxCost() {
    return maxCost == null || maxCost.compareTo(minCost) >= 0;
  }

  @AssertTrue(message = BeanValidationErrorMessage.INVALID_DATE_RANGE_ERROR_MESSAGE)
  public boolean isStartDateEarlierThanEndDate() {
    return startDate.isBefore(endDate);
  }
}
