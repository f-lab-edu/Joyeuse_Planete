package com.f_lab.joyeuse_planete.core.util.log;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogUtil {
  public static void exception(String method, Throwable e) {
    log.error("오류가 발생하였습니다. method = {}, message = {}", method, e.getMessage(), e);
  }

  public static void retry(int attempts, int maxAttempts, String method) {
    log.warn("시도 횟수={}/{}, 메서드={}", attempts, maxAttempts, method);
  }

  public static void deadLetterMissingFormats(String exceptionName, String exceptionMessage, String originalTopic) {
    log.warn("DEAD LETTER TOPIC 에서 오류가 발생하였습니다. topic = {}, name = {}, message = {}", originalTopic, exceptionName, exceptionMessage);
  }

  public static void deadLetterSaveFail(String exceptionName, String exceptionMessage, String originalTopic) {
    log.warn("DEAD LETTER TOPIC 에서 오류가 발생하였습니다. topic = {}, name = {}, message = {}", originalTopic, exceptionName, exceptionMessage);
  }

  public static void info(String method, Object object) {
    log.error("오류가 발생하였습니다.  method = {} object = {}", method, object);
  }
}
