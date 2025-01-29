package com.f_lab.joyeuse_planete.core.kafka.aspect;

import com.f_lab.joyeuse_planete.core.exceptions.JoyeusePlaneteApplicationException;
import com.f_lab.joyeuse_planete.core.exceptions.ErrorCode;
import com.f_lab.joyeuse_planete.core.util.log.LogUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.KafkaException;
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
    int attempts = 0;

    while (attempts < MAX_RETRY) {
      try {
        return joinPoint.proceed();
      } catch (KafkaException e) {
        LogUtil.retry(++attempts, joinPoint.getSignature().toString());

        try {
          Thread.sleep(STOP_INTERVAL);
        } catch (InterruptedException ex) {
          LogUtil.exception(joinPoint.getSignature().toString(), ex);
          throw new JoyeusePlaneteApplicationException(ErrorCode.KAFKA_RETRY_FAIL_EXCEPTION, ex);
        }
      } catch (Throwable e) {
        LogUtil.exception(joinPoint.getSignature().toString(), e);
        throw new JoyeusePlaneteApplicationException(ErrorCode.KAFKA_RETRY_FAIL_EXCEPTION, e);
      }
    }

    throw new JoyeusePlaneteApplicationException(ErrorCode.KAFKA_RETRY_FAIL_EXCEPTION);
  }

}
