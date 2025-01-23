package com.f_lab.la_planete.core.aspect;

import com.f_lab.la_planete.core.exceptions.ApplicationException;
import com.f_lab.la_planete.core.exceptions.ErrorCode;
import com.f_lab.la_planete.core.util.time.TimeConstantsString;
import jakarta.persistence.LockTimeoutException;
import jakarta.persistence.PessimisticLockException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LockRetryAspect {

  @Value("${lock.max.retry:2}")
  private int MAX_RETRY;
  private static final int FIRST_WAIT_INTERVAL = Integer.parseInt(TimeConstantsString.ONE_SECOND);
  private static final int MULTIPLIER = 2;

  @Around("@annotation(com.f_lab.la_planete.core.aspect.RetryOnLockFailure)")
  public Object lockRetry(ProceedingJoinPoint joinPoint) {
    int attempts = 0, stopInterval = FIRST_WAIT_INTERVAL;

    while (attempts < MAX_RETRY) {
      try {
        return joinPoint.proceed();
      } catch (PessimisticLockException | LockTimeoutException e) {
        attempts++;
        log.warn("시도 횟수={}, 다시 메서드={} 락을 얻기를 시도합니다",
            attempts, joinPoint.getSignature());

        // 재시도 전 잠시 멈추고 다시 시작
        // 각 시도 마다 WAIT_INTERVAL 이 MULTIPLIER 에 상응하는 값을 지수적으로 늘어납니다 (backoff)
        // synchronised retry 를 피하기 위해 random 값을 기존 WAIT_INTERVAL 에서 10% ~ 30% 상응하는 값을 더합니다.
        try {
          double PERCENTAGE = 0.1 + (Math.random() * 0.2);
          int RANDOM_FACTOR = (int) (stopInterval * PERCENTAGE);
          stopInterval = stopInterval * (int) Math.pow(MULTIPLIER, attempts) - RANDOM_FACTOR;
          Thread.sleep(stopInterval);

        } catch (InterruptedException ex) {
          throw new ApplicationException(ErrorCode.LOCK_ACQUISITION_FAIL_EXCEPTION, e);
        }

      } catch (Throwable e) {
        log.error("예상치 못한 오류가 발생하였습니다. 다시 시도해 주세요", e);
        throw new ApplicationException(ErrorCode.LOCK_ACQUISITION_FAIL_EXCEPTION, e);
      }
    }

    throw new ApplicationException(ErrorCode.LOCK_ACQUISITION_FAIL_EXCEPTION);
  }
}
