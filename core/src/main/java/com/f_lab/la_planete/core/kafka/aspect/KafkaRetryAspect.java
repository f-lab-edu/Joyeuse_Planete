package com.f_lab.la_planete.core.kafka.aspect;

import com.f_lab.la_planete.core.kafka.exceptions.NonRetryableException;
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

  @Around("@annotation(com.f_lab.la_planete.core.kafka.aspect.KafkaRetry)")
  public Object kafkaRetry(ProceedingJoinPoint joinPoint) {
    int attempt = 0;

    while(attempt < MAX_RETRY) {
      try {
        return joinPoint.proceed();
      } catch (NonRetryableException e) {
        throw e;

      } catch (Exception e) {
        log.warn("시도 횟수={}, 메서드={}", attempt, joinPoint.getSignature());
        attempt++;

        try {
          Thread.sleep(STOP_INTERVAL);
        } catch (InterruptedException ex) {
          throw new RuntimeException(ex);
        }

      } catch (Throwable e) {
        throw new NonRetryableException(e);
      }
    }

    throw new NonRetryableException("오류 발생! 잠시 후 다시 시도해주세요.");
  }
}
