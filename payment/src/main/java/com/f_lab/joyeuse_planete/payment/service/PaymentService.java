package com.f_lab.joyeuse_planete.payment.service;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Timed("payment")
@Service
@RequiredArgsConstructor
public class PaymentService {

  public void process() {

  }
}
