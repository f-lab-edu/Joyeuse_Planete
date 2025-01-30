package com.f_lab.joyeuse_planete.orders.repository;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;

@EmbeddedKafka
@SpringBootTest
class OrderRepositoryTest {

  @Autowired
  OrderRepository orderRepository;
}