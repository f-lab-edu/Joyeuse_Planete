package com.f_lab.joyeuse_planete.foods.service;


import com.f_lab.joyeuse_planete.core.domain.Food;
import com.f_lab.joyeuse_planete.core.domain.FoodOrderReserve;

import com.f_lab.joyeuse_planete.core.events.FoodReservationFailedEvent;
import com.f_lab.joyeuse_planete.core.events.FoodReservationProcessedEvent;
import com.f_lab.joyeuse_planete.core.events.OrderCreatedEvent;
import com.f_lab.joyeuse_planete.core.exceptions.TransactionRollbackException;
import com.f_lab.joyeuse_planete.foods.repository.FoodOrderReserveRepository;
import com.f_lab.joyeuse_planete.foods.repository.FoodRepository;
import com.f_lab.joyeuse_planete.foods.service.handler.OrderCreatedEventHandler;
import com.f_lab.joyeuse_planete.foods.service.listener.FoodEventListener;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.stereotype.Repository;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Slf4j
@SpringBootTest
@DirtiesContext
@EmbeddedKafka
public class FoodServiceEventDuplicationTest {

  @Autowired
  OrderCreatedEventHandler orderCreatedEventHandler;

  @Autowired
  FoodOrderReserveRepository foodOrderReserveRepository;

  @Autowired
  TestFoodRepository foodTestRepository;

  @Autowired
  FoodRepository foodRepository;

  @Spy
  ApplicationEventPublisher applicationEventPublisher;

  @MockitoSpyBean
  FoodEventListener foodEventListener;

  @TestConfiguration
  static class TestConfig {
    @Bean
    public TestFoodRepository foodTestRepository(EntityManager em) {
      return new TestFoodRepository(em);
    }
  }

  @BeforeEach
  void beforeEach() {
    Food food = Food.builder()
        .totalQuantity(100)
        .build();

    foodTestRepository.save(food);
  }

  @DisplayName("여러개의 이벤트가 중복으로 발행되었을 때 하나의 이벤트만 process 한 후 나머진 반영하지 않는다.")
  @Test
  void testDuplicateEventProcessingReflectSingleEventSuccess() throws Throwable {
    // given
    Long foodId = 1L;
    Long orderId = 1L;
    int quantity = 1;

    OrderCreatedEvent orderCreatedEvent = createOrderCreatedEvent(foodId, orderId, quantity);

    int DUPLICATE_EVENTS = 5;
    ExecutorService executorService = Executors.newFixedThreadPool(DUPLICATE_EVENTS);
    CountDownLatch countDownLatch = new CountDownLatch(DUPLICATE_EVENTS);

    for (int i = 0; i < DUPLICATE_EVENTS; i++) {
      executorService.submit(() -> {
        try {
          orderCreatedEventHandler.reserveFoodAfterOrderCreatedEvent(orderCreatedEvent);
          Thread.sleep(500);
        } catch (TransactionRollbackException e) {
          log.error("예외 발생");

        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        } finally {
          countDownLatch.countDown();
        }
      });
    }

    countDownLatch.await();

    Food food = foodRepository.findById(foodId).get();

    // then
    assertThat(food.getTotalQuantity()).isEqualTo(99);
    verify(applicationEventPublisher, times(0)).publishEvent(any());
    verify(foodEventListener, times(0)).on(any(FoodReservationFailedEvent.class));
  }

  private static OrderCreatedEvent createOrderCreatedEvent(Long foodId, Long orderId, int quantity) {
    return new OrderCreatedEvent(foodId, orderId, quantity);
  }

  @Repository
  @Transactional
  @RequiredArgsConstructor
  static class TestFoodRepository {

    private final EntityManager em;

    public Food save(Food food) {
      em.persist(food);
      return food;
    }
  }
}
