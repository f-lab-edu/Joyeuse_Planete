package com.f_lab.la_planete.aspect;

import com.f_lab.la_planete.util.time.TimeConstantsString;
import jakarta.persistence.LockTimeoutException;
import jakarta.persistence.PessimisticLockException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LockRetryAspect {
  private static final int MAX_RETRY = 3;
  private static final int STOP_TIME = Integer.parseInt(TimeConstantsString.ONE_SECOND);

  @Around("@annotation(com.f_lab.la_planete.aspect.RetryOnLockFailure)")
  public Object lockRetry(ProceedingJoinPoint joinPoint) {
    int attempts = 0;

    while (attempts < MAX_RETRY) {
      try {
        return joinPoint.proceed();
      } catch (PessimisticLockException | LockTimeoutException e) {
        attempts++;
        log.warn("시도 횟수={}, 다시 메서드={} 파라미터={} 락을 얻기를 시도합니다",
            attempts, joinPoint.getSignature(),joinPoint.getArgs());

        // 재시도 전 잠시 멈추고 다시 시작
        try {
          Thread.sleep(STOP_TIME);

        } catch (InterruptedException ex) {
          throw new RuntimeException(ex);
        }

      } catch (Throwable e) {
        log.error("예상치 못한 오류가 발생하였습니다. 다시 시도해 주세요", e);
        throw new RuntimeException(e);
      }
    }

    throw new RuntimeException("현재 너무 많은 요청을 처리하고 있습니다. 다시 시도해주세요");
  }
}
