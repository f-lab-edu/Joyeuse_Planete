package com.f_lab.joyeuse_planete.orders.dto.response;

import com.f_lab.joyeuse_planete.core.util.web.ResultResponse.CommonResponses;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class OrderCreateResponseDTO {

  private String message;

  @JsonProperty("status_code")
  private int statusCode;

  @JsonProperty("polling_url")
  private String pollingUrl;

  @JsonIgnore
  private static final String BASE_URL = "/api/v1/orders/";

  @JsonIgnore
  private static final String POLLING_URL = "/order-status";

  public static OrderCreateResponseDTO of(Long orderId) {
    return new OrderCreateResponseDTO(
        CommonResponses.CREATE_SUCCESS,
        HttpStatus.CREATED.value(),
        BASE_URL + orderId + POLLING_URL);
  }
}
