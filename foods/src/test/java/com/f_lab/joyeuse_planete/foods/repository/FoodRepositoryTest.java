package com.f_lab.joyeuse_planete.foods.repository;

import com.f_lab.joyeuse_planete.core.domain.Currency;
import com.f_lab.joyeuse_planete.core.domain.Food;
import com.f_lab.joyeuse_planete.core.domain.Store;
import com.f_lab.joyeuse_planete.foods.dto.request.FoodSearchCondition;
import com.f_lab.joyeuse_planete.foods.dto.response.FoodDTO;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext
@Transactional
@SpringBootTest
class FoodRepositoryTest {

  @Autowired
  FoodRepository foodRepository;

  @Autowired
  private EntityManager em;

  @BeforeEach
  void beforeEach() {
    foodRepository.saveAll(getFoodList());
  }

  @AfterEach
  void afterEach() {
    em.createQuery("DELETE FROM Food").executeUpdate();
    em.createQuery("DELETE FROM Store").executeUpdate();
    em.createQuery("DELETE FROM Currency").executeUpdate();
    em.flush();
  }

  @Test
  @DisplayName("기본 조건으로 검색했을 때 성공하는 것을 확인")
  void testDefaultFoodSearchConditionSuccess() {
    // given
    FoodSearchCondition condition = createDefaultSearchCondition();
    Pageable pageable = PageRequest.of(condition.getPage(), condition.getSize());

    List<FoodDTO> expected = getFoodList().stream()
        .sorted(Comparator.comparing(Food::getRate).reversed())
        .map(FoodDTO::from)
        .limit(condition.getSize())
        .toList();

    // when
    Page<FoodDTO> result = foodRepository.getFoodList(condition, pageable);

    // then
    assertTrue(result, expected);
  }

  @Test
  @DisplayName("음식 검색 조건을 변경하였을 때 작동하는 것을 확인")
  void testFoodSearchConditionOnSearchSuccess() {
    // given
    String search = "Asian";
    List<String> sortBy = List.of("RATE_HIGH");

    FoodSearchCondition condition = createSearchCondition(null, null, search, sortBy);
    Pageable pageable = PageRequest.of(condition.getPage(), condition.getSize());

    List<FoodDTO> expected = getFoodList().stream()
        .filter(f ->
            f.getFoodName().toLowerCase().contains(search.toLowerCase()) ||
                f.getTags().stream().anyMatch(tag -> tag.toLowerCase().contains(search.toLowerCase())) ||
                f.getStore().getName().toLowerCase().contains(search.toLowerCase()))
        .sorted(Comparator.comparing(Food::getRate).reversed())
        .map(FoodDTO::from)
        .limit(condition.getSize())
        .toList();

    // when
    Page<FoodDTO> result = foodRepository.getFoodList(condition, pageable);

    // then
    assertTrue(result, expected);
  }

  @Test
  @DisplayName("페이지 조건을 변경하였을 때 작동하는 것을 확인")
  void testFoodSearchConditionOnPageSuccess() {
    // given
    List<String> sortBy = List.of("RATE_HIGH");
    int page = 1;
    int size = 10;

    FoodSearchCondition condition = createSearchCondition(null, null, null, sortBy);
    Pageable pageable = PageRequest.of(page, size);

    List<FoodDTO> expected = getFoodList().stream()
        .sorted(Comparator.comparing(Food::getRate).reversed())
        .skip(page * size)
        .limit(size)
        .map(FoodDTO::from)
        .toList();

    // when
    Page<FoodDTO> result = foodRepository.getFoodList(condition, pageable);

    // then
    assertTrue(result, expected);
  }

  @Test
  @DisplayName("가짜 + 진짜 정렬 조건을 부여했을 때 무시 후 정상 작동")
  void testNonDefinedSortBySuccess() {
    // given
    List<String> sortBy = List.of("FAKE_SORT_BY", "RATE_HIGH", "NO_RATE");
    int page = 1;
    int size = 10;

    FoodSearchCondition condition = createSearchCondition(null, null, null, sortBy);
    Pageable pageable = PageRequest.of(page, size);

    List<FoodDTO> expected = getFoodList().stream()
        .sorted(Comparator.comparing(Food::getRate).reversed())
        .skip(page * size)
        .limit(size)
        .map(FoodDTO::from)
        .toList();

    // when
    Page<FoodDTO> result = foodRepository.getFoodList(condition, pageable);

    // then
    assertTrue(result, expected);
  }

  @Test
  @DisplayName("가짜 정렬 조건만을 부여했을 때 무시 후 정상 작동")
  void testNonDefinedSortByOnlySuccess() {
    // given
    String search = "Asian";
    List<String> sortBy = List.of("RATE_NO", "NO_RATE");

    FoodSearchCondition condition = createSearchCondition(null, null, search, sortBy);
    Pageable pageable = PageRequest.of(condition.getPage(), condition.getSize());

    List<FoodDTO> expected = getFoodList().stream()
        .filter(f ->
            f.getFoodName().toLowerCase().contains(search.toLowerCase()) ||
                f.getTags().stream().anyMatch(tag -> tag.toLowerCase().contains(search.toLowerCase())) ||
                f.getStore().getName().toLowerCase().contains(search.toLowerCase()))
        .sorted(Comparator.comparing(Food::getRate).reversed())
        .map(FoodDTO::from)
        .limit(condition.getSize())
        .toList();

    // when
    Page<FoodDTO> result = foodRepository.getFoodList(condition, pageable);

    // then
    assertTrue(result, expected);
  }

  private void assertTrue(Page<FoodDTO> result, List<FoodDTO> expected) {
    assertThat(result.getContent())
        .usingRecursiveComparison()
        .withComparatorForType(BigDecimal::compareTo, BigDecimal.class)
        .withComparatorForType(Double::compare, Double.class)
        .comparingOnlyFields("rate")
        .isEqualTo(expected);
  }

  private FoodSearchCondition createDefaultSearchCondition() {
    return new FoodSearchCondition();
  }

  private FoodSearchCondition createSearchCondition(
      @Nullable Double lat,
      @Nullable Double lon,
      @Nullable String search,
      @Nullable List<String> sortBy
  ) {
    FoodSearchCondition condition = new FoodSearchCondition();
    condition.setLat(lat);
    condition.setLon(lon);
    condition.setSearch(search);
    condition.setSortBy(sortBy);
    return condition;
  }

  private List<Food> getFoodList() {
    Store store1 = Store.builder().name("Store A").build();
    Store store2 = Store.builder().name("Store B").build();
    Store store3 = Store.builder().name("Store C").build();
    Store store4 = Store.builder().name("Store Asian").build();
    Store store5 = Store.builder().name("Store Asian").build();

    em.persist(store1);
    em.persist(store2);
    em.persist(store3);
    em.persist(store4);
    em.persist(store5);
    em.flush();

    Currency currency = Currency.builder()
        .currencyCode("KRW")
        .currencySymbol("₩")
        .roundingScale(2)
        .roundingMode(RoundingMode.FLOOR)
        .build();

    em.persist(currency);
    em.flush();

    return List.of(
        Food.builder()
            .foodName("Chicken Burger")
            .price(BigDecimal.valueOf(1000))
            .rate(BigDecimal.valueOf(4.5))
            .store(store1)

            .totalQuantity(100)
            .tags(List.of("fast-food", "burger", "chicken"))
            .build(),

        Food.builder()
            .foodName("Spaghetti Carbonara")
            .price(BigDecimal.valueOf(1200))
            .rate(BigDecimal.valueOf(4.2))
            .store(store2)

            .totalQuantity(80)
            .tags(List.of("pasta", "creamy", "Italian"))
            .build(),

        Food.builder()
            .foodName("Caesar Salad")
            .price(BigDecimal.valueOf(900))
            .rate(BigDecimal.valueOf(4.8))
            .store(store3)

            .totalQuantity(90)
            .tags(List.of("healthy", "salad", "fresh"))
            .build(),

        Food.builder()
            .foodName("Pepperoni Pizza")
            .price(BigDecimal.valueOf(1500))
            .rate(BigDecimal.valueOf(4.7))
            .store(store1)
            .totalQuantity(60)
            .tags(List.of("pizza", "cheesy", "Italian"))
            .build(),

        Food.builder()
            .foodName("Grilled Steak")
            .price(BigDecimal.valueOf(2500))
            .rate(BigDecimal.valueOf(4.9))
            .store(store2)
            .totalQuantity(50)
            .tags(List.of("meat", "grill", "protein"))
            .build(),

        Food.builder()
            .foodName("Tuna Sandwich")
            .price(BigDecimal.valueOf(800))
            .rate(BigDecimal.valueOf(3.9))
            .store(store3)
            .totalQuantity(110)
            .tags(List.of("sandwich", "fish", "healthy"))
            .build(),

        Food.builder()
            .foodName("Vegetable Stir Fry")
            .price(BigDecimal.valueOf(950))
            .rate(BigDecimal.valueOf(4.1))
            .store(store1)
            .totalQuantity(85)
            .tags(List.of("vegetarian", "stir-fry", "Asian"))
            .build(),

        Food.builder()
            .foodName("Beef Tacos")
            .price(BigDecimal.valueOf(1100))
            .rate(BigDecimal.valueOf(4.3))
            .store(store2)
            .totalQuantity(70)
            .tags(List.of("Mexican", "taco", "spicy"))
            .build(),

        Food.builder()
            .foodName("Chicken Noodles")
            .price(BigDecimal.valueOf(1050))
            .rate(BigDecimal.valueOf(4.2))
            .store(store3)
            .totalQuantity(75)
            .tags(List.of("Asian", "noodles", "chicken"))
            .build(),

        Food.builder()
            .foodName("Margarita Pizza")
            .price(BigDecimal.valueOf(1400))
            .rate(BigDecimal.valueOf(4.6))
            .store(store1)
            .totalQuantity(65)
            .tags(List.of("pizza", "tomato", "cheese"))
            .build(),

        Food.builder()
            .foodName("Sushi Rolls")
            .price(BigDecimal.valueOf(1600))
            .rate(BigDecimal.valueOf(4.8))
            .store(store2)
            .totalQuantity(55)
            .tags(List.of("sushi", "fish", "Japanese"))
            .build(),

        Food.builder()
            .foodName("Chocolate Cake")
            .price(BigDecimal.valueOf(950))
            .rate(BigDecimal.valueOf(4.8))
            .store(store2)
            .totalQuantity(65)
            .tags(List.of("dessert", "chocolate", "sweet"))
            .build(),

        Food.builder()
            .foodName("Vanilla Ice Cream")
            .price(BigDecimal.valueOf(500))
            .rate(BigDecimal.valueOf(4.6))
            .store(store3)
            .totalQuantity(130)
            .tags(List.of("dessert", "ice-cream", "cold"))
            .build(),

        Food.builder()
            .foodName("Strawberry Cheesecake")
            .price(BigDecimal.valueOf(1100))
            .rate(BigDecimal.valueOf(4.7))
            .store(store1)
            .totalQuantity(70)
            .tags(List.of("dessert", "cake", "strawberry"))
            .build(),

        Food.builder()
            .foodName("Grilled Chicken Breast")
            .price(BigDecimal.valueOf(1350))
            .rate(BigDecimal.valueOf(4.5))
            .store(store2)
            .totalQuantity(55)
            .tags(List.of("protein", "chicken", "healthy"))
            .build(),

        Food.builder()
            .foodName("Mushroom Soup")
            .price(BigDecimal.valueOf(900))
            .rate(BigDecimal.valueOf(4.3))
            .store(store3)
            .totalQuantity(95)
            .tags(List.of("soup", "warm", "healthy"))
            .build(),

        Food.builder()
            .foodName("Butter Chicken")
            .price(BigDecimal.valueOf(1250))
            .rate(BigDecimal.valueOf(4.6))
            .store(store1)
            .totalQuantity(85)
            .tags(List.of("Indian", "spicy", "curry"))
            .build(),

        Food.builder()
            .foodName("Garlic Bread")
            .price(BigDecimal.valueOf(700))
            .rate(BigDecimal.valueOf(4.2))
            .store(store2)
            .totalQuantity(120)
            .tags(List.of("bread", "garlic", "side-dish"))
            .build()
    );
  }
}