package com.f_lab.joyeuse_planete.orders.controller;

import com.f_lab.joyeuse_planete.core.exceptions.JoyeusePlaneteApplicationException;
import com.f_lab.joyeuse_planete.core.util.log.LogUtil;
import com.f_lab.joyeuse_planete.core.util.web.ResultResponse;
import com.f_lab.joyeuse_planete.core.util.web.ResultResponse.CommonErrorResponses;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;


@Slf4j
@Hidden
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(JoyeusePlaneteApplicationException.class)
  public ResponseEntity<ResultResponse> handle(JoyeusePlaneteApplicationException e) {
    LogUtil.exception("GlobalExceptionHandler.handle (JoyeusePlaneteApplicationException)", e);

    return ResponseEntity
        .status(e.getErrorCode().getStatus())
        .body(ResultResponse.of(e.getErrorCode().getDescription(), e.getErrorCode().getStatus()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ResultResponse> handle(MethodArgumentNotValidException e) {
    LogUtil.exception("GlobalExceptionHandler.handle (MethodArgumentNotValidException)", e);
    StringBuilder sb = new StringBuilder();

    for (ObjectError error : e.getBindingResult().getAllErrors()) {
      sb.append(error.getDefaultMessage()).append("\n");
    }

    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(ResultResponse.of(sb.toString().trim(), HttpStatus.BAD_REQUEST.value()));
  }

  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<ResultResponse> handelResourceNotFoundException() {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ResultResponse.of(CommonErrorResponses.INCORRECT_ADDRESS, HttpStatus.NOT_FOUND.value()));
  }

  /**
   * 어플리케이션에서 처리하지 못한 다른 모든 예외
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ResultResponse> handleGenericException(Exception e) {
    log.error("Unexpected Error occurred. Requires Potential Handling", e);
    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ResultResponse.of(
            "알 수 없는 오류가 발생했습니다. 다시 시도해주세요",
            HttpStatus.INTERNAL_SERVER_ERROR.value()));
  }
}
