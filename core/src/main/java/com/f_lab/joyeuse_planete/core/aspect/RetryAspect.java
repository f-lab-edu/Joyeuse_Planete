package com.f_lab.joyeuse_planete.core.aspect;

import com.f_lab.joyeuse_planete.core.annotation.Backoff;
import com.f_lab.joyeuse_planete.core.annotation.Retry;
import com.f_lab.joyeuse_planete.core.backoff.BackOffPolicy;
import com.f_lab.joyeuse_planete.core.backoff.ExponentialBackOff;
import com.f_lab.joyeuse_planete.core.backoff.FixedBackOff;
import com.f_lab.joyeuse_planete.core.exceptions.ErrorCode;
import com.f_lab.joyeuse_planete.core.exceptions.JoyeusePlaneteApplicationException;
import com.f_lab.joyeuse_planete.core.util.log.LogUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order
@Aspect
@Component
public class RetryAspect {

  @Around("@annotation(retry)")
  public Object retry(ProceedingJoinPoint joinPoint, Retry retry) throws Throwable {
    BackOffPolicy backOffPolicy = getBackOffPolicy(retry.backoff());

    for (int attempts = 1; attempts <= retry.value(); attempts++) {
      try {
        return joinPoint.proceed();

      } catch (Exception e) {
        if (isNotRetryable(e, retry.nonRetryable()) || !isRetryable(e, retry.retryable()))
          throw e;

        LogUtil.retry(attempts, retry.value(), joinPoint.getSignature().toString());
        backOffPolicy.apply(attempts);

        if (attempts >= retry.value())
          throw new JoyeusePlaneteApplicationException(ErrorCode.UNKNOWN_EXCEPTION, e);
      }
    }

    throw new JoyeusePlaneteApplicationException(ErrorCode.UNKNOWN_EXCEPTION);
  }

  private boolean isRetryable(Exception e, Class<? extends Exception>[] retryableExceptions) {
    for (Class<? extends Exception> retryable : retryableExceptions) {
      if (retryable.isAssignableFrom(e.getClass()))
        return true;
    }

    return false;
  }

  private boolean isNotRetryable(Exception e, Class<? extends Exception>[] nonRetryableExceptions) {
    for (Class<? extends Exception> notRetryable : nonRetryableExceptions) {
      if (notRetryable.isAssignableFrom(e.getClass()))
        return true;
    }

    return false;
  }

  private BackOffPolicy getBackOffPolicy(Backoff backoff) {
    return (backoff.multiplier() > 0)
        ? new ExponentialBackOff(backoff.multiplier(), backoff.delay())
        : new FixedBackOff(backoff.delay());
  }
}
