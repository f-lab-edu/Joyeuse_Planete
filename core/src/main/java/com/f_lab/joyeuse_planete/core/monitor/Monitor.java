package com.f_lab.joyeuse_planete.core.monitor;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Monitor {

  private final MeterRegistry meterRegistry;

  public TimedAspect timedAspect() {
    return new TimedAspect(meterRegistry);
  }
}
