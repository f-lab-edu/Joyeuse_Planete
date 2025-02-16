package com.f_lab.joyeuse_planete.payment.service;

import com.f_lab.joyeuse_planete.core.domain.Payment;
import com.f_lab.joyeuse_planete.core.domain.PaymentStatus;
import com.f_lab.joyeuse_planete.core.events.PaymentProcessedEvent;
import com.f_lab.joyeuse_planete.core.exceptions.ErrorCode;
import com.f_lab.joyeuse_planete.core.exceptions.JoyeusePlaneteApplicationException;
import com.f_lab.joyeuse_planete.core.kafka.service.KafkaService;
import com.f_lab.joyeuse_planete.core.util.log.LogUtil;
import com.f_lab.joyeuse_planete.payment.repository.PaymentRepository;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Timed("payment")
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PaymentService {

  private final PaymentRepository paymentRepository;
  private final KafkaService kafkaService;

  @Value("${payment.events.topics.process}")
  private String PAYMENT_PROCESS_EVENT;

  @Transactional
  public void processPaymentSuccess(String paymentKey, Long orderId, BigDecimal amount, String processor) {
    try {
      paymentRepository.save(paymentKey, processor, orderId, amount, PaymentStatus.DONE.toString());

      //      "https://api.tosspayments.com/v1/payments/confirm" 에 결제성공 로직 구현
    } catch (Exception e) {
      LogUtil.exception("PaymentService.processPaymentSuccess", e);
      throw new RuntimeException(e);
    }

    sendKafkaPaymentProcessedEvent(null);
  }

  @Transactional
  public void processPaymentFailure(Long orderId, String code, String message, String processor) {
    try {
      paymentRepository.save("null", processor, orderId, BigDecimal.ZERO, PaymentStatus.ABORTED.toString());

    } catch(Exception e) {
      LogUtil.exception("PaymentService.processPaymentFailure", e);
      throw new RuntimeException(e);
    }
  }

  @Transactional
  public void processRefund(Long paymentId) {
    Payment payment = findPaymentById(paymentId);
  }

  public void sendKafkaPaymentProcessedEvent(PaymentProcessedEvent event) {
    try {
      kafkaService.sendKafkaEvent(PAYMENT_PROCESS_EVENT, event);
    } catch(Exception e) {
      LogUtil.exception("PaymentService.sendKafkaEvent", e);
    }
  }

  private Payment findPaymentById(Long paymentId) {
    return paymentRepository.findById(paymentId).orElseThrow(
        () -> new JoyeusePlaneteApplicationException(ErrorCode.PAYMENT_NOT_EXIST_EXCEPTION)
    );
  }
}
