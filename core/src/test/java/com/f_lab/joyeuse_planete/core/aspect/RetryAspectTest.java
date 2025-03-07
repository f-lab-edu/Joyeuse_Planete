package com.f_lab.joyeuse_planete.core.aspect;

import com.f_lab.joyeuse_planete.core.annotation.Retry;
import com.f_lab.joyeuse_planete.core.exceptions.JoyeusePlaneteApplicationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


class RetryAspectTest {

  RetryTestService proxy;

  @BeforeEach
  void beforeEach() {
    RetryTestService service = spy(new RetryTestService());
    AspectJProxyFactory factory = new AspectJProxyFactory(service);
    factory.addAspect(RetryAspect.class);
    proxy = factory.getProxy();
  }

  @DisplayName("일반로직의 성공 상황")
  @Test
  void testRetrySpringAopSuccess() {
    proxy.retrySuccess();
    verify(proxy, times(1)).retrySuccess();
  }

  @DisplayName("일반로직의 실패 & Retry 스프링 AOP 작동 확인 3번 retry 확인")
  @Test
  void testRetrySpringAopFailWithErrorAndTryThreeTimes() {
    assertThrows(JoyeusePlaneteApplicationException.class, () -> proxy.retryError());
    verify(proxy, times(3)).retryError();
  }

  @DisplayName("일반로직의 실패 & Retry 스프링 AOP 작동 확인 2번 retry 확인")
  @Test
  void testRetrySpringAopFailWithErrorAndTryTwoTimes() {
    assertThrows(JoyeusePlaneteApplicationException.class, () -> proxy.retryErrorTwice());
    verify(proxy, times(2)).retryErrorTwice();
  }

  @DisplayName("일반로직의 실패 & Retry 스프링 AOP 작동 실패 Retry 하지 않는 예외")
  @Test
  void testRetrySpringAopFailWithErrorNotRetryingException() {
    assertThrows(RuntimeException.class, () -> proxy.retryErrorIllegal());
    verify(proxy, times(1)).retryErrorIllegal();
  }

  @DisplayName("일반로직의 실패 & Retry 스프링 AOP 작동 확인 2번 retry 확인")
  @Test
  void testRetrySpringAopFailWithErrorAndTryTwoTimesIllegalStateException() {
    assertThrows(RuntimeException.class, () -> proxy.retryErrorTwiceIllegalFail());
    verify(proxy, times(1)).retryErrorTwiceIllegalFail();
  }

  @DisplayName("일반로직의 실패 & Retry 스프링 AOP 작동 확인 4번 retry 확인 및 다른 예외상황 확인")
  @Test
  void testRetrySpringAopFailWithErrorAndTryFourTimesIllegalStateException() {
    assertThrows(JoyeusePlaneteApplicationException.class, () -> proxy.retryErrorTwiceIllegalSuccess());
    verify(proxy, times(4)).retryErrorTwiceIllegalSuccess();
  }


  static class RetryTestService {

    @Retry
    public void retrySuccess() {
    }

    @Retry
    public void retryError() {
      throw new RuntimeException();
    }

    @Retry(value = 2)
    public void retryErrorTwice() {
      throw new RuntimeException();
    }

    @Retry(retryable = { IllegalStateException.class })
    public void retryErrorIllegal() {
      throw new RuntimeException();
    }

    @Retry(value = 2, retryable = { IllegalStateException.class })
    public void retryErrorTwiceIllegalFail() {
      throw new RuntimeException();
    }

    @Retry(value = 4, retryable = { IllegalStateException.class })
    public void retryErrorTwiceIllegalSuccess() {
      throw new IllegalStateException();
    }
  }
}