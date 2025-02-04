package com.f_lab.joyeuse_planete.foods.controller;

import com.f_lab.joyeuse_planete.foods.domain.FoodSearchCondition;
import com.f_lab.joyeuse_planete.foods.dto.request.CreateFoodRequestDTO;
import com.f_lab.joyeuse_planete.foods.dto.request.UpdateFoodRequestDTO;
import com.f_lab.joyeuse_planete.foods.dto.response.FoodDTO;
import com.f_lab.joyeuse_planete.foods.service.FoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static com.f_lab.joyeuse_planete.foods.controller.FoodController.ResponseUtil.FOOD_CREATE_SUCCESS;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/foods")
public class FoodController {

  private final FoodService foodService;

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public Page<FoodDTO> getFoodList(@ModelAttribute FoodSearchCondition condition) {
    Pageable pageable = PageRequest.of(condition.getPage(), condition.getSize());
    return foodService.getFoodList(condition, pageable);
  }

  @GetMapping("/{foodId}")
  public ResponseEntity<FoodDTO> getFood(@PathVariable("foodId") Long foodId) {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(foodService.getFood(foodId));
  }

  @PostMapping
  public ResponseEntity<String> createFood(@RequestBody CreateFoodRequestDTO request) {
    foodService.createFood(request);

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(FOOD_CREATE_SUCCESS);
  }

  @PutMapping("/{foodId}")
  public ResponseEntity<String> updateFood(@PathVariable Long foodId,
                                           @RequestBody UpdateFoodRequestDTO request) {
    foodService.updateFood(foodId, request);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(ResponseUtil.FOOD_UPDATE_SUCCESS);
  }

  @DeleteMapping("/{foodId}")
  public ResponseEntity<String> deleteFood(@PathVariable Long foodId) {
    foodService.deleteFood(foodId);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(ResponseUtil.FOOD_DELETE_SUCCESS);
  }

  static abstract class ResponseUtil {

    public static final String FOOD_CREATE_SUCCESS = "성공적으로 생성되었습니다.";
    public static final String FOOD_UPDATE_SUCCESS = "업데이트 요청이 완료되었습니다.";
    public static final String FOOD_DELETE_SUCCESS = "삭제 요청이 완료되었습니다.";
  }
}
