package com.f_lab.joyeuse_planete.foods.service;

import com.f_lab.joyeuse_planete.core.domain.Currency;
import com.f_lab.joyeuse_planete.core.domain.Food;
import com.f_lab.joyeuse_planete.core.exceptions.ErrorCode;
import com.f_lab.joyeuse_planete.core.exceptions.JoyeusePlaneteApplicationException;
import com.f_lab.joyeuse_planete.foods.domain.FoodSearchCondition;
import com.f_lab.joyeuse_planete.foods.dto.request.CreateFoodRequestDTO;
import com.f_lab.joyeuse_planete.foods.dto.request.UpdateFoodRequestDTO;
import com.f_lab.joyeuse_planete.foods.dto.response.FoodDTO;
import com.f_lab.joyeuse_planete.foods.repository.CurrencyRepository;
import com.f_lab.joyeuse_planete.foods.repository.FoodRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FoodService {

  private final FoodRepository foodRepository;
  private final CurrencyRepository currencyRepository;

  public FoodDTO getFood(Long foodId) {
    return FoodDTO.from(findFood(foodId));
  }

  public Page<FoodDTO> getFoodList(FoodSearchCondition condition, Pageable pageable) {
    return foodRepository.getFoodList(condition, pageable);
  }

  @Transactional
  public void createFood(CreateFoodRequestDTO request) {
    Currency currency = findCurrency(request.getCurrencyCode());
    Food food = request.toEntity(currency);

    foodRepository.save(food);
  }

  @Transactional
  public void deleteFood(Long foodId) {
    foodRepository.delete(findFood(foodId));
  }

  @Transactional
  public void updateFood(Long foodId, UpdateFoodRequestDTO request) {
    Food food = findFood(foodId);
    Currency currency = findCurrency(request.getCurrencyCode());

    food.update(
        request.getFoodName(),
        request.getPrice(),
        request.getTotalQuantity(),
        currency,
        request.getCollectionStartTime(),
        request.getCollectionEndTime()
    );

    foodRepository.save(food);
  }

  @Transactional
  public void reserve(Long foodId, int quantity) {
    Food food = findFoodWithLock(foodId);
    food.minusQuantity(quantity);
    foodRepository.save(food);
  }

  @Transactional
  public void release(Long foodId, int quantity) {
    Food food = findFoodWithLock(foodId);
    food.plusQuantity(quantity);
    foodRepository.save(food);
  }

  private Food findFood(Long foodId) {
    return foodRepository.findById(foodId)
        .orElseThrow(() -> new JoyeusePlaneteApplicationException(ErrorCode.FOOD_NOT_EXIST_EXCEPTION));
  }

  private Food findFoodWithLock(Long foodId) {
    return foodRepository.findFoodByFoodIdWithPessimisticLock(foodId)
        .orElseThrow(() -> new JoyeusePlaneteApplicationException(ErrorCode.FOOD_NOT_EXIST_EXCEPTION));
  }

  private Currency findCurrency(String currencyCode) {
    return currencyRepository.findByCurrencyCode(currencyCode)
        .orElseThrow(() -> new JoyeusePlaneteApplicationException(ErrorCode.CURRENCY_NOT_EXIST_EXCEPTION));
  }
}
