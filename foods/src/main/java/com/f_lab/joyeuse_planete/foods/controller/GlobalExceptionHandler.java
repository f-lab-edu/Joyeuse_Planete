package com.f_lab.joyeuse_planete.foods.controller;

import com.f_lab.joyeuse_planete.core.exceptions.JoyeusePlaneteApplicationException;
import com.f_lab.joyeuse_planete.core.util.log.LogUtil;
import com.f_lab.joyeuse_planete.core.util.web.ResultResponse;
import com.f_lab.joyeuse_planete.core.util.web.ResultResponse.CommonErrorResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;


@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(JoyeusePlaneteApplicationException.class)
  public ResponseEntity<ResultResponse> handle(JoyeusePlaneteApplicationException e) {
    LogUtil.exception("GlobalExceptionHandler.handle (JoyeusePlaneteApplicationException)", e);

    return ResponseEntity
        .status(e.getErrorCode().getStatus())
        .body(ResultResponse.of(e.getErrorCode().getDescription(), e.getErrorCode().getStatus()));
  }

  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<ResultResponse> handle(NoResourceFoundException e) {
    LogUtil.exception("GlobalExceptionHandler.handle (NoResourceFoundException)", e);

    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(ResultResponse.of(CommonErrorResponses.INCORRECT_ADDRESS, HttpStatus.NOT_FOUND.value()));
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

  /**
   * 데이터베이스 관련 예외 (e.g. 주로 락을 얻는 대기시간이 오래되어서 요청을 반환할 때)
   */
  @ExceptionHandler(JpaSystemException.class)
  public ResponseEntity<ResultResponse> handle(JpaSystemException e) {
    LogUtil.exception("GlobalExceptionHandler.handle (JpaSystemException)", e);

    return ResponseEntity
        .status(HttpStatus.SERVICE_UNAVAILABLE)
        .body(ResultResponse.of(
            CommonErrorResponses.TOO_MANY_REQUEST,
            HttpStatus.SERVICE_UNAVAILABLE.value()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ResultResponse> handle(Exception e) {
    LogUtil.exception("GlobalExceptionHandler.handle (Exception)", e);

    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ResultResponse.of(
            CommonErrorResponses.DEFAULT_MESSAGE,
            HttpStatus.INTERNAL_SERVER_ERROR.value()));
  }
}
