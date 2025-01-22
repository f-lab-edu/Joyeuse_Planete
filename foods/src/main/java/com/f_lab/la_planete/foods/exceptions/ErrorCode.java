package com.f_lab.la_planete.foods.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static lombok.AccessLevel.PRIVATE;

@Getter
@AllArgsConstructor(access = PRIVATE)
public enum ErrorCode {
  FOOD_NOT_EXIST_EXCEPTION("상품이 존재하지 않습니다.", HttpStatus.BAD_REQUEST),

  ;

  private String description;
  private HttpStatus httpStatus;
}
