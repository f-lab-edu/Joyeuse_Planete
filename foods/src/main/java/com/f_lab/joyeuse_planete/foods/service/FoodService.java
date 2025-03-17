package com.f_lab.joyeuse_planete.foods.service;

import com.f_lab.joyeuse_planete.core.annotation.Backoff;
import com.f_lab.joyeuse_planete.core.annotation.Retry;
import com.f_lab.joyeuse_planete.core.domain.Food;
import com.f_lab.joyeuse_planete.core.exceptions.ErrorCode;
import com.f_lab.joyeuse_planete.core.exceptions.JoyeusePlaneteApplicationException;
import com.f_lab.joyeuse_planete.foods.dto.request.FoodSearchCondition;
import com.f_lab.joyeuse_planete.foods.dto.request.CreateFoodRequestDTO;
import com.f_lab.joyeuse_planete.foods.dto.request.UpdateFoodRequestDTO;
import com.f_lab.joyeuse_planete.foods.dto.response.FoodDTO;
import com.f_lab.joyeuse_planete.foods.repository.FoodRepository;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.hibernate.PessimisticLockException;
import org.hibernate.exception.LockTimeoutException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Timed("foods")
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FoodService {

  private final FoodRepository foodRepository;

  @Cacheable(value = "food", key = "#foodId")
  public FoodDTO getFood(Long foodId) {
    return FoodDTO.from(findFood(foodId));
  }

//  @Cacheable(value = "foods", keyGenerator = "foodSearchKeyGenerator")
  public Page<FoodDTO> getFoodList(FoodSearchCondition condition, Pageable pageable) {
    return foodRepository.getFoodList(condition, pageable);
  }

  @Transactional
  public void createFood(CreateFoodRequestDTO request) {
    Food food = request.toEntity();
    foodRepository.save(food);
  }

  @CacheEvict(value = "food", key = "#foodId")
  @Transactional
  public void deleteFood(Long foodId) {
    foodRepository.delete(findFood(foodId));
  }

  @CachePut(value = "food", key = "#foodId")
  @Transactional
  public FoodDTO updateFood(Long foodId, UpdateFoodRequestDTO request) {
    Food food = findFood(foodId);

    food.update(
        request.getFoodName(),
        request.getPrice(),
        request.getTotalQuantity(),
        request.getCollectionStartTime(),
        request.getCollectionEndTime()
    );

    return FoodDTO.from(foodRepository.save(food));
  }

  @CachePut(value = "food", key = "#foodId")
  @Transactional
  public FoodDTO reserve(Long foodId, int quantity) {
    Food food = findFoodWithLock(foodId);

    food.minusQuantity(quantity);
    foodRepository.save(food);

    return FoodDTO.from(food);
  }

  @CachePut(value = "food", key = "#foodId")
  @Transactional
  public FoodDTO release(Long foodId, int quantity) {
    Food food = findFoodWithLock(foodId);
    food.plusQuantity(quantity);

    return FoodDTO.from(foodRepository.save(food));
  }

  private Food findFood(Long foodId) {
    return foodRepository.findById(foodId)
        .orElseThrow(() -> new JoyeusePlaneteApplicationException(ErrorCode.FOOD_NOT_EXIST_EXCEPTION));
  }

  private Food findFoodWithLock(Long foodId) {
    return foodRepository.findFoodByFoodIdWithPessimisticLock(foodId)
        .orElseThrow(() -> new JoyeusePlaneteApplicationException(ErrorCode.FOOD_NOT_EXIST_EXCEPTION));
  }
}
