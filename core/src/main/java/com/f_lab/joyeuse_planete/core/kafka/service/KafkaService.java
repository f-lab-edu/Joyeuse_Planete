package com.f_lab.joyeuse_planete.core.kafka.service;

import com.f_lab.joyeuse_planete.core.annotation.Backoff;
import com.f_lab.joyeuse_planete.core.annotation.Retry;
import com.f_lab.joyeuse_planete.core.exceptions.ErrorCode;
import com.f_lab.joyeuse_planete.core.exceptions.JoyeusePlaneteApplicationException;
import com.f_lab.joyeuse_planete.core.kafka.exceptions.RetryableException;
import com.f_lab.joyeuse_planete.core.util.log.LogUtil;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.errors.BrokerNotAvailableException;
import org.apache.kafka.common.errors.DisconnectException;
import org.apache.kafka.common.errors.NetworkException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;


@RequiredArgsConstructor
public class KafkaService {

  private final KafkaTemplate<String, Object> kafkaTemplate;

  @Retry(retryable = { KafkaException.class }, backoff = @Backoff(multiplier = 2))
  @Transactional(propagation = REQUIRES_NEW)
  public void sendKafkaEvent(String event, Object object) {
    try {
      kafkaTemplate.send(event, object);

    } catch(DisconnectException | NetworkException | BrokerNotAvailableException e) {
      LogUtil.exception("KafkaService.sendKafkaEvent (DisconnectException | NetworkException | BrokerNotAvailableException)", e);
      throw new JoyeusePlaneteApplicationException(ErrorCode.KAFKA_UNAVAILABLE_EXCEPTION);

    } catch(Exception e) {
      LogUtil.exception("KafkaService.sendKafkaEvent (Exception)", e);
      throw new RetryableException();
    }
  }
}
