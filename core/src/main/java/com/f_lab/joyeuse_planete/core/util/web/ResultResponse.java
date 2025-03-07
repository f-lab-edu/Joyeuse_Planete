package com.f_lab.joyeuse_planete.core.util.web;

import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultResponse {

  private Object message;

  @JsonProperty("status_code")
  private int statusCode;

  public static ResultResponse of(String message, int statusCode) {
    return new ResultResponse(message, statusCode);
  }

  public static ResultResponse of(List<String> message, int statusCode) {
    return new ResultResponse(message, statusCode);
  }

  @JsonIgnoreType
  public static abstract class CommonErrorResponses {
    public static final String INCORRECT_ADDRESS = "올바르지 않은 주소입니다. 다시 확인해주세요.";
    public static final String DEFAULT_MESSAGE = "알 수 없는 오류가 발생했습니다. 다시 시도해주세요";
    public static final String TOO_MANY_REQUEST = "현재 너무 많은 요청을 처리하고 있습니다. 다시 시도해주세요.";
  }

  @JsonIgnoreType
  public static abstract class CommonResponses {
    public static final String CREATE_SUCCESS = "성공적으로 생성되었습니다.";
    public static final String UPDATE_SUCCESS = "업데이트 요청이 완료되었습니다.";
    public static final String DELETE_SUCCESS = "삭제 요청이 완료되었습니다.";
    public static final String PONG = "pong";
  }
}
