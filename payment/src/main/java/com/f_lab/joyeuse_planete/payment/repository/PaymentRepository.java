package com.f_lab.joyeuse_planete.payment.repository;

import com.f_lab.joyeuse_planete.core.domain.Payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;


public interface PaymentRepository extends JpaRepository<Payment, Long> {

  @Query(value = "INSERT INTO Payment p (payment_key, processor, order_id, total_cost, status) VALUES (:paymentKey, :processor, :orderId, totalCost, status)", nativeQuery = true)
  Payment save(@Param("paymentKey") String paymentKey, @Param("processor") String processor, @Param("orderId") Long orderId, @Param("totalCost") BigDecimal totalCost, @Param("status") String status);
}
