package com.f_lab.joyeuse_planete.payment.service;

import com.f_lab.joyeuse_planete.core.domain.Payment;
import com.f_lab.joyeuse_planete.core.domain.PaymentStatus;
import com.f_lab.joyeuse_planete.core.events.PaymentOrRefundProcessedEvent;
import com.f_lab.joyeuse_planete.core.events.PaymentOrRefundProcessingFailedEvent;
import com.f_lab.joyeuse_planete.core.exceptions.ErrorCode;
import com.f_lab.joyeuse_planete.core.exceptions.JoyeusePlaneteApplicationException;
import com.f_lab.joyeuse_planete.core.kafka.service.KafkaService;
import com.f_lab.joyeuse_planete.core.util.log.LogUtil;
import com.f_lab.joyeuse_planete.payment.repository.PaymentRepository;
import com.f_lab.joyeuse_planete.payment.service.thirdparty.PaymentManagerService;
import com.f_lab.joyeuse_planete.payment.service.thirdparty.exceptions.PaymentNonRetryableException;
import com.f_lab.joyeuse_planete.payment.service.thirdparty.exceptions.PaymentRetryableException;
import com.f_lab.joyeuse_planete.payment.service.thirdparty.response.PaymentResponse;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;


@Timed("payment")
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PaymentService {

  private static final String TOSS = "토스";
  private static final String KAKAO = "카카오";

  private final PaymentRepository paymentRepository;
  private final PaymentManagerService paymentManagerService;
  private final KafkaService kafkaService;

  @Value("${payment.events.topics.process}")
  private String PAYMENT_PROCESS_EVENT;

  @Value("${payment.events.topics.process-fail}")
  private String PAYMENT_PROCESS_FAIL_EVENT;

  @Value("${payment.events.topics.refund}")
  private String PAYMENT_REFUND_PROCESSED_EVENT;

  @Value("${payment.events.topics.refund-fail}")
  private String PAYMENT_REFUND_FAIL_EVENT;


  @Transactional
  public void processPaymentSuccessToss(String paymentKey, Long orderId, BigDecimal amount) {
    paymentRepository.save(paymentKey, TOSS, orderId, amount, PaymentStatus.IN_PROGRESS.toString());
    Payment payment = findPaymentByPaymentKey(paymentKey);
    Mono<PaymentResponse> result = paymentManagerService.processPayment(payment);

    result.subscribe(
        res -> handleSuccessfulPayment(payment),
        e -> handleFailedPayment(payment, orderId, e));
  }

  @Transactional
  public void processPaymentFailureToss(Long orderId, String code, String message) {
    try {
      paymentRepository.save("null", TOSS, orderId, BigDecimal.ZERO, PaymentStatus.ABORTED.toString());
    } catch(Exception e) {
      LogUtil.exception("PaymentService.processPaymentFailureToss", e);
    }

    sendKafkaPaymentEvent(
        PAYMENT_PROCESS_FAIL_EVENT,
        PaymentOrRefundProcessingFailedEvent.toEvent(orderId, ErrorCode.PAYMENT_UNKNOWN_EXCEPTION, false));
  }

  @Transactional
  public void processRefund(Long paymentId) {
    Payment payment = findPaymentById(paymentId);
    payment.setStatus(PaymentStatus.IN_PROGRESS);
    Mono<PaymentResponse> result = paymentManagerService.processRefund(payment);

    result.subscribe(
        res -> handleRefundSuccess(payment),
        e -> handleRefundFailure(payment, e));
  }

  private void handleSuccessfulPayment(Payment payment) {
    payment.setStatus(PaymentStatus.DONE);
    paymentRepository.save(payment);

    sendKafkaPaymentEvent(
        PAYMENT_PROCESS_EVENT,
        PaymentOrRefundProcessedEvent.toEvent(payment.getOrder().getId()));
  }

  private void handleFailedPayment(Payment payment, Long orderId, Throwable e) {
    LogUtil.exception("PaymentService.handleFailedPayment", e);

    payment.setStatus(PaymentStatus.ABORTED);
    paymentRepository.save(payment);

    ErrorCode errorCode = getErrorCodeFromException(e);
    boolean isRetryable = (e instanceof PaymentRetryableException);

    sendKafkaPaymentEvent(
        PAYMENT_PROCESS_FAIL_EVENT,
        PaymentOrRefundProcessingFailedEvent.toEvent(
          orderId,
          errorCode,
          isRetryable
    ));
  }

  private void handleRefundSuccess(Payment payment) {
    payment.setStatus(PaymentStatus.REFUND_DONE);
    paymentRepository.save(payment);
    sendKafkaPaymentEvent(
        PAYMENT_REFUND_PROCESSED_EVENT,
        PaymentOrRefundProcessedEvent.toEvent(payment.getOrder().getId()));
  }

  private void handleRefundFailure(Payment payment, Throwable e) {
    LogUtil.exception("PaymentProcessor.handleRefundFailure", e);

    payment.setStatus(PaymentStatus.REFUND_ABORTED);
    paymentRepository.save(payment);

    ErrorCode errorCode = getErrorCodeFromException(e);
    boolean isRetryable = true;

    sendKafkaPaymentEvent(
        PAYMENT_REFUND_FAIL_EVENT,
        PaymentOrRefundProcessingFailedEvent.toEvent(
            payment.getOrder().getId(),
            errorCode,
            isRetryable
        ));
  }

  public void sendKafkaPaymentEvent(String event, Object payload) {
    try {
      kafkaService.sendKafkaEvent(event, payload);
    } catch(Exception e) {
      LogUtil.exception("PaymentService.sendKafkaPaymentEvent", e);
    }
  }

  private Payment findPaymentById(Long paymentId) {
    return paymentRepository.findById(paymentId).orElseThrow(
        () -> new JoyeusePlaneteApplicationException(ErrorCode.PAYMENT_NOT_EXIST_EXCEPTION)
    );
  }

  private Payment findPaymentByPaymentKey(String paymentKey) {
    return paymentRepository.findByPaymentKey(paymentKey).orElseThrow(
        () -> new JoyeusePlaneteApplicationException(ErrorCode.PAYMENT_NOT_EXIST_EXCEPTION)
    );
  }

  private ErrorCode getErrorCodeFromException(Throwable e) {
    if (e instanceof PaymentRetryableException) {
      return ((PaymentRetryableException) e).getErrorCode();
    }

    else if (e instanceof PaymentNonRetryableException) {
      return ((PaymentNonRetryableException) e).getErrorCode();
    }

    return ErrorCode.UNKNOWN_EXCEPTION;
  }
}
