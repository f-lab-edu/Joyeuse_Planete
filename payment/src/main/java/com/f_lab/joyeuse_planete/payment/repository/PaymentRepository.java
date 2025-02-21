package com.f_lab.joyeuse_planete.payment.repository;

import com.f_lab.joyeuse_planete.core.domain.Payment;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;


public interface PaymentRepository extends JpaRepository<Payment, Long> {

  @Modifying
  @Query(value = "INSERT INTO payments (payment_key, processor, order_id, total_cost, status) VALUES (:paymentKey, :processor, :orderId, :totalCost, :status)", nativeQuery = true)
  void save(@Param("paymentKey") String paymentKey, @Param("processor") String processor, @Param("orderId") Long orderId, @Param("totalCost") BigDecimal totalCost, @Param("status") String status);

  Optional<Payment> findByPaymentKey(@Param("paymentKey") String paymentKey);

  // TODO storeId 필요
  @Override
  @EntityGraph(attributePaths = { "order" })
  Optional<Payment> findById(Long paymentId);
}
