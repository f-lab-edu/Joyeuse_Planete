package com.f_lab.joyeuse_planete.members.controller;

import com.f_lab.joyeuse_planete.core.exceptions.JoyeusePlaneteApplicationException;
import com.f_lab.joyeuse_planete.core.util.log.LogUtil;
import com.f_lab.joyeuse_planete.core.util.web.BeanValidationErrorMessage;
import com.f_lab.joyeuse_planete.core.util.web.ResultResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {


  @ExceptionHandler(JoyeusePlaneteApplicationException.class)
  public ResponseEntity<ResultResponse> handle(JoyeusePlaneteApplicationException e) {
    LogUtil.exception("GlobalExceptionHandler.handle (JoyeusePlaneteApplicationException)", e);

    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(ResultResponse.of(e.getErrorCode().getDescription(), HttpStatus.BAD_REQUEST.value()));
  }

  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<ResultResponse> handle(NoResourceFoundException e) {
    LogUtil.exception("GlobalExceptionHandler.handle (NoResourceFoundException)", e);

    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(ResultResponse.of(ResultResponse.CommonErrorResponses.INCORRECT_ADDRESS, HttpStatus.NOT_FOUND.value()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ResultResponse> handle(MethodArgumentNotValidException e) {
    LogUtil.exception("GlobalExceptionHandler.handle (MethodArgumentNotValidException)", e);

    String errorMessages = e.getBindingResult().getAllErrors()
        .stream()
        .map(err -> err != null ? err.getDefaultMessage() : BeanValidationErrorMessage.DEFAULT_ERROR_MESSAGE)
        .collect(Collectors.joining("\n"));

    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(ResultResponse.of(errorMessages, HttpStatus.BAD_REQUEST.value()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ResultResponse> handle(Exception e) {
    LogUtil.exception("GlobalExceptionHandler.handle (Exception)", e);

    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ResultResponse.of(
            ResultResponse.CommonErrorResponses.DEFAULT_MESSAGE,
            HttpStatus.INTERNAL_SERVER_ERROR.value()));
  }
}
