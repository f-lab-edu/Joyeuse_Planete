package com.f_lab.joyeuse_planete.foods.domain;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class FoodSearchCondition {
  // TODO 거리 추가 구현
  // 위도 경도 DEFAULT SET TO LONDON
  Double lat = 51.5072;
  Double lon = -0.118092;
  String search;
  int page = 0;
  int size = 25;
  List<String> sortBy = List.of("RATE_HIGH");
}
