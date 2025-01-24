package com.f_lab.joyeuse_planete.foods.service;

import com.f_lab.joyeuse_planete.core.kafka.aspect.KafkaRetry;
import com.f_lab.joyeuse_planete.core.kafka.exceptions.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaHandlerService {

  private final KafkaTemplate<String, Object> kafkaTemplate;

  @KafkaRetry
  @Transactional(propagation = REQUIRES_NEW)
  public void sendKafkaEvent(String event, Object object) {
    try {
      kafkaTemplate.send(event, object);
    } catch(Exception e) {
      log.error("오류가 발생하였습니다. message = {}", e.getMessage(), e);
      throw new RetryableException();
    }
  }

  /**
   * DeadLetterQueue 에 대한 구현이 안되어 있어서 아직은 technical debt로 남겨둠
   */
  public void sendKafkaFailureEvent(String event, Object object) {
    kafkaTemplate.send(event, object);
  }
}
