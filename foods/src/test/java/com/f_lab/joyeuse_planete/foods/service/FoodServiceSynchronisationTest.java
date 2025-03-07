package com.f_lab.joyeuse_planete.foods.service;

import com.f_lab.joyeuse_planete.core.domain.Food;
import com.f_lab.joyeuse_planete.core.domain.Store;
import com.f_lab.joyeuse_planete.foods.repository.FoodRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Repository;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext
@SpringBootTest
class FoodServiceSynchronisationTest {

  @Autowired
  FoodService foodService;

  @Autowired
  FoodRepository foodRepository;

  @Autowired
  TestStoreRepository storeRepository;

  @TestConfiguration
  static class TestConfig {
    @Bean
    public TestStoreRepository storeRepository(EntityManager em) {
      return new TestStoreRepository(em);
    }
  }

  Food dummyFood = Food.builder()
      .price(BigDecimal.valueOf(1000))
      .totalQuantity(1000)
      .build();

  @BeforeEach
  void beforeEach() {
    Store store = storeRepository.save(Store.builder().build());
    dummyFood.setStore(store);
    foodRepository.saveAndFlush(dummyFood);
  }

  @Test
  @DisplayName("동시성 테스트 100개의 요청이 동시에 왔을 때 데이터의 일관성이 유지")
  void test_concurrency_thread_100_success() throws InterruptedException {
    // given
    int count = 100;
    CountDownLatch countDownLatch = new CountDownLatch(count);
    ExecutorService executorService = Executors.newFixedThreadPool(count);

    // when
    for (int i = 0; i < count; i++) {
      executorService.submit(() -> {
        try {
          foodService.reserve(1L, 10);
        } finally {
          countDownLatch.countDown();
        }
      });
    }

    countDownLatch.await();

    // then
    Food food = foodRepository.findById(1L).orElseThrow();
    assertThat(food.getTotalQuantity()).isEqualTo(0L);
  }

  @Repository
  @Transactional
  @RequiredArgsConstructor
  static class TestStoreRepository {

    private final EntityManager em;

    public Store save(Store store) {
      em.persist(store);
      return store;
    }
  }
}