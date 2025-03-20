package com.f_lab.joyeuse_planete.payment.service.listener;

import com.f_lab.joyeuse_planete.core.events.PaymentProcessedEvent;
import com.f_lab.joyeuse_planete.core.events.PaymentProcessingFailedEvent;
import com.f_lab.joyeuse_planete.core.events.RefundProcessedEvent;
import com.f_lab.joyeuse_planete.core.events.RefundProcessingFailedEvent;
import com.f_lab.joyeuse_planete.core.kafka.service.KafkaService;
import com.f_lab.joyeuse_planete.core.util.log.LogUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@Component
@RequiredArgsConstructor
public class PaymentEventListener {

  private final KafkaService kafkaService;

  @Value("${payment.events.topics.process}")
  private String PAYMENT_PROCESS_EVENT;

  @Value("${payment.events.topics.process-fail}")
  private String PAYMENT_PROCESS_FAIL_EVENT;

  @Value("${payment.events.topics.refund}")
  private String PAYMENT_REFUND_PROCESSED_EVENT;

  @Value("${payment.events.topics.refund-fail}")
  private String PAYMENT_REFUND_FAIL_EVENT;

  @Async
  @TransactionalEventListener(phase = AFTER_COMMIT)
  public void on(PaymentProcessedEvent event) {
    try {
      kafkaService.sendKafkaEvent(PAYMENT_PROCESS_EVENT, event);
    } catch (Exception e) {
      LogUtil.exception("PaymentEventListener.on (PaymentProcessedEvent)", e);
    }
  }

  @Async
  @TransactionalEventListener(phase = AFTER_COMMIT)
  public void on(PaymentProcessingFailedEvent event) {
    try {
      kafkaService.sendKafkaEvent(PAYMENT_PROCESS_FAIL_EVENT, event);
    } catch (Exception e) {
      LogUtil.exception("PaymentEventListener.on (PaymentProcessingFailedEvent)", e);
    }
  }

  @Async
  @TransactionalEventListener(phase = AFTER_COMMIT)
  public void on(RefundProcessedEvent event) {
    try {
      kafkaService.sendKafkaEvent(PAYMENT_REFUND_PROCESSED_EVENT, event);
    } catch (Exception e) {
      LogUtil.exception("PaymentEventListener.on (RefundProcessedEvent)", e);
    }
  }

  @Async
  @TransactionalEventListener(phase = AFTER_COMMIT)
  public void on(RefundProcessingFailedEvent event) {
    try {
      kafkaService.sendKafkaEvent(PAYMENT_REFUND_FAIL_EVENT, event);
    } catch (Exception e) {
      LogUtil.exception("PaymentEventListener.on (RefundProcessingFailedEvent)", e);
    }
  }
}
