package com.f_lab.joyeuse_planete.core.kafka.util;

import com.f_lab.joyeuse_planete.core.exceptions.ErrorCode;

import org.springframework.kafka.listener.ListenerExecutionFailedException;

import java.util.List;

public class ExceptionUtil {
  private static final List<Class<?>> unWrapList = List.of(
      ListenerExecutionFailedException.class
  );

  private static final List<String> nonRequeueList = List.of(
      ErrorCode.FOOD_NOT_ENOUGH_STOCK.getDescription(),
      ErrorCode.FOOD_NOT_EXIST_EXCEPTION.getDescription()
  );

  public static Exception unwrap(Exception e) {
    while (e.getCause() != null && unWrapList.contains(e.getClass())) {
      e = (Exception) e.getCause();
    }
    return e;
  }

  public static boolean noRequeue(String message) {
    return nonRequeueList.contains(message);
  }
}
