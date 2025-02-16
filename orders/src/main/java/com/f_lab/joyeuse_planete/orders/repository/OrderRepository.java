package com.f_lab.joyeuse_planete.orders.repository;


import com.f_lab.joyeuse_planete.core.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;


public interface OrderRepository extends JpaRepository<Order, Long>, OrderRepositoryCustom {

}
