package com.f_lab.joyeuse_planete.core.kafka.domain;

public enum DeadLetterStatus {
  PENDING, REQUEUED, FAILED_INVALID_FOR_REQUEUE, FAILED_AFTER_MAX_RETRIES,
}
