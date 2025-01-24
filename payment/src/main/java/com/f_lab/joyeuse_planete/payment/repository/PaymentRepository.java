package com.f_lab.joyeuse_planete.payment.repository;

import com.f_lab.joyeuse_planete.core.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
