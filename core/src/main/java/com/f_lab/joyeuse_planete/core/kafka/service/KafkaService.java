package com.f_lab.joyeuse_planete.core.kafka.service;

import com.f_lab.joyeuse_planete.core.kafka.aspect.KafkaRetry;
import com.f_lab.joyeuse_planete.core.kafka.exceptions.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@Slf4j
@RequiredArgsConstructor
public class KafkaService {

  private final KafkaTemplate<String, Object> kafkaTemplate;

  @KafkaRetry
  @Transactional(propagation = REQUIRES_NEW)
  public void sendKafkaEvent(String event, Object object) {
    try {
      kafkaTemplate.send(event, object);
    } catch(Exception e) {
      log.error("오류가 발생하였습니다. event = {}, message = {}", event, e.getMessage(), e);
      throw new RetryableException();
    }
  }
}
