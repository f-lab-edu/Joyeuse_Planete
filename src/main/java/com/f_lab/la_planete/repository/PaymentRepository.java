package com.f_lab.la_planete.repository;

import com.f_lab.la_planete.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
