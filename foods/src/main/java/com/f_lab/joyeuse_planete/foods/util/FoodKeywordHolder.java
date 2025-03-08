package com.f_lab.joyeuse_planete.foods.util;

import java.util.List;
import java.util.regex.Pattern;

public class FoodKeywordHolder {

  private static final List<String> keywords = List.of(
      "치킨", "피자", "떡볶이", "김밥", "삼겹살", "불고기", "비빔밥", "라면", "버거",
      "족발", "보쌈", "순대", "갈비", "냉면", "전", "감자탕", "김치찌개", "된장찌개", "한정식",
      "부대찌개", "쭈꾸미", "회", "초밥", "유부초밥", "오므라이스", "스테이크", "파스타",
      "리조또", "수제비", "칼국수", "만두", "찜닭", "쌀국수", "마라탕", "양꼬치",
      "스시", "카레", "버섯전골", "샐러드", "핫도그", "크로와상", "디져트",
      "도넛", "와플", "팥빙수", "호떡", "붕어빵", "계란찜", "갈비탕", "해물찜",
      "아귀찜", "닭강정", "양장피", "깐풍기",
      "떡갈비", "돈가스", "함박스테이크", "순두부찌개", "초코파이", "땅콩", "호두",
      "아이스크림", "푸딩", "모찌", "찹쌀떡", "닭갈비", "낙곱새", "곱창", "막창", "대창",
      "쭈꾸미볶음", "오돌뼈", "치즈볼", "스팸", "콘치즈", "가래떡", "누룽지", "감자튀김",
      "마카롱", "티라미수", "브라우니", "초콜릿", "롤케이크", "커피", "녹차", "밀크티",
      "탄산음료", "콜라", "사이다", "주스", "맥주", "소주", "와인", "칵테일", "막걸리"
  );

  public static List<String> findMatchingKeywords(String input) {
    return keywords.stream()
        .filter(keyword -> Pattern.compile(".*" + keyword + ".*").matcher(input).matches())
        .toList();
  }

  public static List<String> findMatchingKeywords(List<String> input) {
    return keywords.stream()
        .filter(k -> input.stream()
            .anyMatch(i -> Pattern.compile(".*" + k + ".*").matcher(i).matches()))
        .toList();
  }
}
