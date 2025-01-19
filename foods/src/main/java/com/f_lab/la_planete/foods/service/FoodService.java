package com.f_lab.la_planete.foods.service;

import com.f_lab.la_planete.core.domain.Food;
import com.f_lab.la_planete.foods.repository.FoodRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FoodService {

  private final FoodRepository foodRepository;


  @Transactional
  public void reserve(Long foodId, int quantity) {
    Food food = foodRepository.findFoodByFoodIdWithPessimisticLock(foodId);
    food.minusQuantity(quantity);
    foodRepository.save(food);
  }
}
