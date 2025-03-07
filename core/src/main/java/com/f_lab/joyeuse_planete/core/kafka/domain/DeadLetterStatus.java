package com.f_lab.joyeuse_planete.core.kafka.domain;

public enum DeadLetterStatus {
  PENDING, REQUEUED, INVALID_FOR_REQUEUE,
}
