package com.f_lab.la_planete.facade;

import com.f_lab.la_planete.domain.Food;
import com.f_lab.la_planete.repository.FoodRepository;
import jakarta.persistence.LockTimeoutException;
import jakarta.persistence.PessimisticLockException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
class FoodRepositoryFacadeTest {

  @InjectMocks
  FoodRepositoryFacade foodRepositoryFacade;
  @Mock
  FoodRepository foodRepository;

  @Test
  @DisplayName("락 없이 첫 시도에 성공")
  void test_find_food_lock_and_retry_success() {
    // given
    Long foodId = 1L;
    Food expectedFood = createFood(foodId);

    // when
    when(foodRepository.findFoodByFoodIdWithPessimisticLock(anyLong())).thenReturn(expectedFood);
    Food foundFood = foodRepositoryFacade.findFoodWithLockAndRetry(foodId);

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

    Food foundFood = foodRepositoryFacade.findFoodWithLockAndRetry(foodId);

    // then
    assertThat(foundFood.getId()).isEqualTo(foodId);
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

    assertThatThrownBy(() -> foodRepositoryFacade.findFoodWithLockAndRetry(foodId))
        .isInstanceOf(RuntimeException.class)
        .hasMessage("현재 너무 많은 요청을 처리하고 있습니다. 다시 시도해주세요");
  }


  private Food createFood(Long foodId) {
    return Food.builder()
        .id(foodId)
        .build();
  }
}