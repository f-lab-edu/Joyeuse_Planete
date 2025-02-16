package com.f_lab.joyeuse_planete.foods.service;

import com.f_lab.joyeuse_planete.core.domain.Currency;
import com.f_lab.joyeuse_planete.core.domain.Food;
import com.f_lab.joyeuse_planete.core.domain.Store;
import com.f_lab.joyeuse_planete.core.exceptions.ErrorCode;
import com.f_lab.joyeuse_planete.core.exceptions.JoyeusePlaneteApplicationException;
import com.f_lab.joyeuse_planete.foods.dto.request.FoodSearchCondition;
import com.f_lab.joyeuse_planete.foods.dto.request.UpdateFoodRequestDTO;
import com.f_lab.joyeuse_planete.foods.dto.response.FoodDTO;
import com.f_lab.joyeuse_planete.foods.repository.FoodRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FoodServiceTest {

  @InjectMocks
  FoodService foodService;
  @Mock
  FoodRepository foodRepository;

  @Test
  @DisplayName("getFood() 호출시 성공")
  void testGetFoodSuccess() {
    Long foodId = 1L;
    Food food = createFood(foodId);
    FoodDTO expected = createExpectedFoodDTO(food);

    // when
    when(foodRepository.findById(anyLong())).thenReturn(Optional.of(food));
    FoodDTO result = foodService.getFood(foodId);

    // then
    assertThat(result).isEqualTo(expected);
  }

  @Test
  @DisplayName("존재하지 않는 food 를 getFood 통해서 호출시 실패")
  void testGetFoodOnNotExistingFoodFail() {
    // given
    Long foodId = 1L;

    // when
    when(foodRepository.findById(anyLong())).thenReturn(Optional.empty());

    // then
    assertThatThrownBy(() -> foodService.getFood(foodId))
        .isInstanceOf(JoyeusePlaneteApplicationException.class)
        .hasMessage(ErrorCode.FOOD_NOT_EXIST_EXCEPTION.getDescription());
  }

  @Test
  @DisplayName("deleteFood() 호출시 성공")
  void deleteFoodSuccess() {
    // Given
    Long foodId = 1L;
    Food food = createFood(foodId);

    when(foodRepository.findById(foodId)).thenReturn(Optional.of(food));

    // When
    foodService.deleteFood(foodId);

    // Then
    verify(foodRepository).delete(food);
  }

  @Test
  @DisplayName("reserve() 호출시 성공")
  void testReserveSuccess() {
    // given
    Long foodId = 1L;
    int quantity = 1;
    Food food = createFood(foodId);
    int totalQuantity = food.getTotalQuantity();

    // when
    when(foodRepository.findFoodByFoodIdWithPessimisticLock(anyLong())).thenReturn(Optional.of(food));
    foodService.reserve(foodId, quantity);

    // then
    assertThat(food.getTotalQuantity()).isEqualTo(totalQuantity - quantity);
  }

  @Test
  @DisplayName("release() 호출시 성공")
  void testReleaseSuccess() {
    // given
    Long foodId = 1L;
    int quantity = 1;
    Food food = createFood(foodId);
    int totalQuantity = food.getTotalQuantity();

    // when
    when(foodRepository.findFoodByFoodIdWithPessimisticLock(anyLong())).thenReturn(Optional.of(food));
    foodService.release(foodId, quantity);

    // then
    assertThat(food.getTotalQuantity()).isEqualTo(totalQuantity + quantity);
  }

  @Test
  @DisplayName("updateFood() 호출시 성공")
  void testUpdateFoodSuccess() {
    // given
    Long foodId = 1L;
    Food food = createFood(foodId);
    String expectedCurrencyCode = "USD";
    Currency currency = createCurrency();
    String originalName = food.getFoodName();

    UpdateFoodRequestDTO request = createExpectedUpdateFoodRequestDTO(food);

    // when
    when(foodRepository.findById(anyLong())).thenReturn(Optional.of(food));
    foodService.updateFood(foodId, request);

    // then
    assertThat(food.getFoodName()).isEqualTo(originalName + "test");
    assertThat(food.getCurrency().getCurrencyCode()).isEqualTo(expectedCurrencyCode);
  }


  @Test
  @DisplayName("foodService 가 올바로 foodRepository 호출하고 Page를 return 하는 것을 확인")
  void testGetFoodListSuccess() {
    // given
    Page<FoodDTO> expected = Page.empty();
    FoodSearchCondition condition = new FoodSearchCondition();
    Pageable pageable = PageRequest.of(0, 10);

    // when
    when(foodRepository.getFoodList(any(), any())).thenReturn(expected);
    Page<FoodDTO> result = foodService.getFoodList(condition, pageable);

    // then
    assertThat(result).isEqualTo(expected);
    verify(foodRepository, times(1)).getFoodList(condition, pageable);
  }

  private Food createFood(Long foodId) {
    String foodName = "Pizza";
    BigDecimal price = new BigDecimal("9.99");
    int totalQuantity = 50;

    return Food.builder()
        .id(foodId)
        .store(createStore())
        .currency(createCurrency())
        .foodName(foodName)
        .rate(BigDecimal.valueOf(4.5))
        .price(price)
        .totalQuantity(totalQuantity)
        .build();
  }

  private Store createStore() {
    Long storeId = 1L;

    return Store.builder()
        .id(storeId)
        .build();
  }

  private Currency createCurrency() {
    Long currencyId = 100L;
    String currencyCode = "USD";
    String currencySymbol = "$";

    return Currency.builder()
        .id(currencyId)
        .currencyCode(currencyCode)
        .currencySymbol(currencySymbol)
        .build();
  }

  private UpdateFoodRequestDTO createExpectedUpdateFoodRequestDTO(Food food) {
    String TEST_SUFFIX = "test";
    String currencyCode = "USD";

    return UpdateFoodRequestDTO.builder()
        .foodName(food.getFoodName() + TEST_SUFFIX)
        .currencyCode(currencyCode)
        .price(food.getPrice())
        .totalQuantity(food.getTotalQuantity())
        .build();
  }

  private FoodDTO createExpectedFoodDTO(Food food) {
    return FoodDTO.from(food);
  }
}