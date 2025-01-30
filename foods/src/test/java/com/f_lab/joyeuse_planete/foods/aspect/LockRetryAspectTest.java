package com.f_lab.joyeuse_planete.foods.aspect;

import com.f_lab.joyeuse_planete.core.aspect.RetryOnLockFailure;
import com.f_lab.joyeuse_planete.core.domain.Food;
import com.f_lab.joyeuse_planete.foods.repository.FoodRepository;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class LockRetryAspectTest {

  @MockitoBean
  private FoodRepository foodRepository;

  @Autowired
  private FoodTestService foodTestService;

  @TestConfiguration
  static class LockRetryAspectTestConfig {
    @Bean
    public FoodTestService foodTestService(FoodRepository foodRepository) {
      return new FoodTestService(foodRepository);
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
        .thenReturn(Optional.ofNullable(expectedFood));

    Food foundFood = foodTestService.findFood(foodId).get();

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
        .thenReturn(Optional.ofNullable(expectedFood));

    Food foundFood = foodTestService.findFood(foodId).get();

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
    assertThatThrownBy(() -> foodTestService.findFood(foodId))
        .isInstanceOf(RuntimeException.class)
        .hasMessage("현재 너무 많은 요청을 처리하고 있습니다. 다시 시도해주세요 (락)");
  }

  private Food createFood(Long foodId) {
    return Food.builder()
        .id(foodId)
        .build();
  }

  @RequiredArgsConstructor
  static class FoodTestService {
    private final FoodRepository foodRepository;

    @RetryOnLockFailure
    public Optional<Food> findFood(Long foodId) {
      return foodRepository.findFoodByFoodIdWithPessimisticLock(foodId);
    }
  }
}