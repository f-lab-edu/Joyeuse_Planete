package com.f_lab.joyeuse_planete.core.backoff;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ExponentialBackOff implements BackOffPolicy {

  private int MULTIPLIER;
  private int DELAY;

  @Override
  public void apply(int attempts) throws InterruptedException {
    // 재시도 전 잠시 멈추고 다시 시작
    // 각 시도 마다 WAIT_INTERVAL 이 MULTIPLIER 에 상응하는 값을 지수적으로 늘어납니다 (backoff)
    // synchronised retry 를 피하기 위해 random 값을 기존 WAIT_INTERVAL 에서 10% ~ 30% 상응하는 값을 더합니다.

    int stopInterval = DELAY;
    double PERCENTAGE = 0.1 + (Math.random() * 0.2);
    int RANDOM_FACTOR = (int) (stopInterval * PERCENTAGE);
    stopInterval = stopInterval * (int) Math.pow(MULTIPLIER, attempts) - RANDOM_FACTOR;
    Thread.sleep(stopInterval);
  }
}
