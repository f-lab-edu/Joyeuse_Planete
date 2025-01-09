package com.f_lab.la_planete.aspect;

import com.f_lab.la_planete.domain.Food;
import com.f_lab.la_planete.repository.FoodRepository;
import jakarta.persistence.LockTimeoutException;
import jakarta.persistence.PessimisticLockException;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@SpringBootTest
class LockRetryAspectTest {

  @MockitoBean
  private FoodRepository foodRepository;

  @Autowired
  private FoodService foodService;

  @TestConfiguration
  static class LockRetryAspectTestConfig {
    @Bean
    public FoodService foodService(FoodRepository foodRepository) {
      return new FoodService(foodRepository);
    }
  }

  @Test
  @DisplayName("락 없이 첫 시도에 성공")
  void test_find_food_lock_and_retry_success() {
    // given
    Long foodId = 1L;
    Food expectedFood = createFood(foodId);

    // when
    when(foodRepository.findFoodByFoodIdWithPessimisticLock(foodId))
        .thenReturn(expectedFood);

    Food foundFood = foodService.findFood(foodId);

    // then
    assertThat(foundFood.getId()).isEqualTo(foodId);
  }

  @Test
  @DisplayName("첫 번째 시도는 실패하고 두 번째 시도에 성공")
  void test_find_food_lock_and_retry_fail_on_first_then_success() {
    // given
    Long foodId = 1L;
    Food expectedFood = createFood(foodId);

    // when
    when(foodRepository.findFoodByFoodIdWithPessimisticLock(anyLong()))
        .thenThrow(new PessimisticLockException())
        .thenReturn(expectedFood);

    Food foundFood = foodService.findFood(foodId);

    // then
    assertThat(foundFood.getId()).isEqualTo(foodId);
    verify(foodRepository, times(2)).findFoodByFoodIdWithPessimisticLock(foodId);
  }

  @Test
  @DisplayName("락 타임아웃으로 최대 재시도 후 실패")
  void test_find_food_lock_and_retry_fail() {
    // given
    Long foodId = 1L;

    // when
    when(foodRepository.findFoodByFoodIdWithPessimisticLock(anyLong()))
        .thenThrow(new PessimisticLockException())
        .thenThrow(new LockTimeoutException())
        .thenThrow(new LockTimeoutException());

    // then
    assertThatThrownBy(() -> foodService.findFood(foodId))
        .isInstanceOf(RuntimeException.class)
        .hasMessage("현재 너무 많은 요청을 처리하고 있습니다. 다시 시도해주세요");
  }

  private Food createFood(Long foodId) {
    return Food.builder()
        .id(foodId)
        .build();
  }

  @RequiredArgsConstructor
  static class FoodService {
    private final FoodRepository foodRepository;

    @RetryOnLockFailure
    public Food findFood(Long foodId) {
      return foodRepository.findFoodByFoodIdWithPessimisticLock(foodId);
    }
  }
}
