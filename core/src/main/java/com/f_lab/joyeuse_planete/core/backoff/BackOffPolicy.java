package com.f_lab.joyeuse_planete.core.backoff;

public interface BackOffPolicy {

  void apply(int attempts) throws InterruptedException;
}
