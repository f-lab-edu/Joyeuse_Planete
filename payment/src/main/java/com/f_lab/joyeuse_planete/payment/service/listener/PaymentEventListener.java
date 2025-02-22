package com.f_lab.joyeuse_planete.payment.service.listener;

import com.f_lab.joyeuse_planete.core.events.PaymentProcessedEvent;
import com.f_lab.joyeuse_planete.core.events.PaymentProcessingFailedEvent;
import com.f_lab.joyeuse_planete.core.events.RefundProcessedEvent;
import com.f_lab.joyeuse_planete.core.events.RefundProcessingFailedEvent;
import com.f_lab.joyeuse_planete.core.kafka.service.KafkaService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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

  @TransactionalEventListener(phase = AFTER_COMMIT)
  public void on(PaymentProcessedEvent event) {
    kafkaService.sendKafkaEvent(PAYMENT_PROCESS_EVENT, event);
  }

  @TransactionalEventListener(phase = AFTER_COMMIT)
  public void on(PaymentProcessingFailedEvent event) {
    kafkaService.sendKafkaEvent(PAYMENT_PROCESS_FAIL_EVENT, event);
  }

  @TransactionalEventListener(phase = AFTER_COMMIT)
  public void on(RefundProcessedEvent event) {
    kafkaService.sendKafkaEvent(PAYMENT_REFUND_PROCESSED_EVENT, event);
  }

  @TransactionalEventListener(phase = AFTER_COMMIT)
  public void on(RefundProcessingFailedEvent event) {
    kafkaService.sendKafkaEvent(PAYMENT_REFUND_FAIL_EVENT, event);
  }
}
