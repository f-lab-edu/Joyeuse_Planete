package com.f_lab.joyeuse_planete.core.backoff;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FixedBackOff implements BackOffPolicy{

  private int DELAY;

  @Override
  public void apply(int attempts) throws InterruptedException {
    Thread.sleep(DELAY);
  }
}
