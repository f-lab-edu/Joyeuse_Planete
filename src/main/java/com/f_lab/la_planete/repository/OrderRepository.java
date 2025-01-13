package com.f_lab.la_planete.repository;


import com.f_lab.la_planete.core.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
