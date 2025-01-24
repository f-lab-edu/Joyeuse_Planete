package com.f_lab.joyeuse_planete.core.kafka.aspect;

import com.f_lab.joyeuse_planete.core.exceptions.ApplicationException;
import com.f_lab.joyeuse_planete.core.exceptions.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class KafkaRetryAspect {

  @Value("${kafka.lock.max.retry:3}")
  private int MAX_RETRY;
  private static final int STOP_INTERVAL = 1000;

  @Around("@annotation(com.f_lab.joyeuse_planete.core.kafka.aspect.KafkaRetry)")
  public Object kafkaRetry(ProceedingJoinPoint joinPoint) {
    int attempt = 0;

    while(attempt < MAX_RETRY) {
      try {
        return joinPoint.proceed();
      } catch (Throwable e) {
        log.warn("시도 횟수={}, 메서드={}", attempt, joinPoint.getSignature());
        attempt++;

        try {
          Thread.sleep(STOP_INTERVAL);
        } catch (InterruptedException ex) {
          throw new ApplicationException(ErrorCode.KAFKA_RETRY_FAIL_EXCEPTION, ex);
        }
      }
    }

    throw new ApplicationException(ErrorCode.KAFKA_RETRY_FAIL_EXCEPTION);
  }
}
