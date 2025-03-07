package com.f_lab.joyeuse_planete.core.kafka.util;

import com.f_lab.joyeuse_planete.core.exceptions.ErrorCode;
import org.springframework.kafka.listener.ListenerExecutionFailedException;

import java.util.Set;

public class ExceptionUtil {
  private static final Set<Class<?>> unWrapList = Set.of(
      ListenerExecutionFailedException.class
  );

  private static final Set<String> nonRequeueList = Set.of(
      ErrorCode.FOOD_NOT_ENOUGH_STOCK.getDescription(),
      ErrorCode.FOOD_NOT_EXIST_EXCEPTION.getDescription(),
      ErrorCode.UNKNOWN_EXCEPTION.getDescription(),
      ErrorCode.KAFKA_RETRY_NOT_ACCEPTABLE_EXCEPTION.getDescription()
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
