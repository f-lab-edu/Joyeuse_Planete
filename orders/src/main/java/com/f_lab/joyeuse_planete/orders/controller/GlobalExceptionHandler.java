package com.f_lab.joyeuse_planete.orders.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@Hidden
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException e) {
    return ResponseEntity
        .status(HttpStatus.CONFLICT)
        .body(ErrorResponse.of(e.getMessage(), HttpStatus.CONFLICT.value()));
  }

  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<ErrorResponse> handelResourceNotFoundException() {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ErrorResponse.of("올바르지 않은 주소입니다. 다시 확인해 주세요", HttpStatus.NOT_FOUND.value()));
  }

  /**
   * 데이터베이스 관련 예외 (e.g. 주로 락을 얻는 대기시간이 오래되어서 요청을 반환할 때)
   */
  @ExceptionHandler(JpaSystemException.class)
  public ResponseEntity<ErrorResponse> handleJpaSystemException(JpaSystemException e) {
    log.error("JpaSystemException occurred", e);
    return ResponseEntity
        .status(HttpStatus.SERVICE_UNAVAILABLE)
        .body(ErrorResponse.of(
            "현재 너무 많은 요청을 처리하고 있습니다. 다시 시도해주세요",
            HttpStatus.SERVICE_UNAVAILABLE.value()));
  }

  /**
   * 어플리케이션에서 처리하지 못한 다른 모든 예외
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {
    log.error("Unexpected Error occurred. Requires Potential Handling", e);
    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ErrorResponse.of(
            "알 수 없는 오류가 발생했습니다. 다시 시도해주세요",
            HttpStatus.INTERNAL_SERVER_ERROR.value()));
  }

  @Getter
  @AllArgsConstructor
  static class ErrorResponse {
    private String message;

    @JsonProperty("status_code")
    private int statusCode;

    public static ErrorResponse of(String message, int statusCode) {
      return new ErrorResponse(message, statusCode);
    }
  }
}
