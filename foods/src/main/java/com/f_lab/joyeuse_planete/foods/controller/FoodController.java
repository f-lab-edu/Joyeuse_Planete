package com.f_lab.joyeuse_planete.foods.controller;

import com.f_lab.joyeuse_planete.core.util.web.ResultResponse;
import com.f_lab.joyeuse_planete.core.util.web.ResultResponse.CommonResponses;
import com.f_lab.joyeuse_planete.foods.dto.request.CreateFoodRequestDTO;
import com.f_lab.joyeuse_planete.foods.dto.request.FoodSearchCondition;
import com.f_lab.joyeuse_planete.foods.dto.request.UpdateFoodRequestDTO;
import com.f_lab.joyeuse_planete.foods.dto.response.FoodDTO;
import com.f_lab.joyeuse_planete.foods.service.FoodService;
import jakarta.validation.Valid;
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



@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/foods")
public class FoodController {

  private final FoodService foodService;

  @GetMapping("/{foodId}")
  public ResponseEntity<FoodDTO> getFood(@PathVariable("foodId") Long foodId) {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(foodService.getFood(foodId));
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public Page<FoodDTO> getFoodList(@ModelAttribute @Valid FoodSearchCondition condition) {
    Pageable pageable = PageRequest.of(condition.getPage(), condition.getSize());
    return foodService.getFoodList(condition, pageable);
  }

  @PostMapping
  public ResponseEntity<ResultResponse> createFood(@RequestBody @Valid CreateFoodRequestDTO request) {
    foodService.createFood(request);

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(ResultResponse.of(CommonResponses.CREATE_SUCCESS, HttpStatus.CREATED.value()));
  }

  @PutMapping("/{foodId}")
  public ResponseEntity<ResultResponse> updateFood(@PathVariable Long foodId,
                                                   @RequestBody @Valid UpdateFoodRequestDTO request
  ) {

    foodService.updateFood(foodId, request);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(ResultResponse.of(CommonResponses.UPDATE_SUCCESS, HttpStatus.OK.value()));
  }

  @DeleteMapping("/{foodId}")
  public ResponseEntity<ResultResponse> deleteFood(@PathVariable Long foodId) {
    foodService.deleteFood(foodId);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(ResultResponse.of(CommonResponses.DELETE_SUCCESS, HttpStatus.OK.value()));
  }

  @GetMapping("/ping")
  public ResponseEntity<ResultResponse> healthcheck() {
    return ResponseEntity.status(HttpStatus.OK)
        .body(ResultResponse.of(CommonResponses.PONG, HttpStatus.OK.value()));
  }
}
