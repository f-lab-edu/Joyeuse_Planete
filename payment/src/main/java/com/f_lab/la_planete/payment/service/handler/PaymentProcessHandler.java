package com.f_lab.la_planete.payment.service.handler;


import com.f_lab.la_planete.core.commands.PaymentProcessCommand;
import com.f_lab.la_planete.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
@KafkaListener(
    topics = { "${payments.commands.topic.name}" },
    groupId = "${spring.kafka.consumer.group-id}"
)
public class PaymentProcessHandler {

  private final PaymentService paymentService;

  @KafkaHandler
  public void handlePaymentProcessCommand(@Payload PaymentProcessCommand paymentProcessCommand) {
    log.info("PaymentProcessCommand={}", paymentProcessCommand);

    try {

    } catch (Exception e) {

    }
  }
}
