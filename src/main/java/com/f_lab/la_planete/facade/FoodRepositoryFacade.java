package com.f_lab.la_planete.facade;

import com.f_lab.la_planete.domain.Food;
import com.f_lab.la_planete.repository.FoodRepository;
import jakarta.persistence.LockTimeoutException;
import jakarta.persistence.PessimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FoodRepositoryFacade {

  private static final int MAX_RETRY = 3;

  private final FoodRepository foodRepository;

  public void save(Food food) {
    foodRepository.save(food);
  }

  public Food findFoodWithLockAndRetry(Long foodId) {
    int attempts = 0;

    while (attempts < MAX_RETRY) {
      try {
        Food food = foodRepository.findFoodByFoodIdWithPessimisticLock(foodId);

        if (food != null)
          return food;

      } catch (PessimisticLockException | LockTimeoutException e) {
        log.warn("시도 횟수={}, 다시 id={} 에 해당되는 food의 락을 얻기를 시도합니다", attempts, foodId);
      } catch (Exception e) {
        log.error("Error Occurred at FoodLockFacade.findFoodWithLockAndRetry", e);
        throw e;
      }

      attempts++;
    }

    throw new RuntimeException("현재 너무 많은 요청을 처리하고 있습니다. 다시 시도해주세요");
  }
}
