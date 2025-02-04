package com.f_lab.joyeuse_planete.foods.repository;

import com.f_lab.joyeuse_planete.core.domain.Currency;
import com.f_lab.joyeuse_planete.core.domain.Food;
import com.f_lab.joyeuse_planete.core.domain.Store;
import com.f_lab.joyeuse_planete.foods.domain.FoodSearchCondition;
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

    for (FoodDTO foodDTO : result) {
      System.out.println("foodDTO = " + foodDTO);
    }

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
        .build();

    em.persist(currency);
    em.flush();

    return List.of(
        Food.builder()
            .foodName("Chicken Burger")
            .price(BigDecimal.valueOf(1000))
            .rate(4.5)
            .store(store1)
            .currency(currency)
            .totalQuantity(100)
            .tags("fast-food,burger,chicken")
            .build(),

        Food.builder()
            .foodName("Spaghetti Carbonara")
            .price(BigDecimal.valueOf(1200))
            .rate(4.2)
            .store(store2)
            .currency(currency)
            .totalQuantity(80)
            .tags("pasta,creamy,Italian")
            .build(),

        Food.builder()
            .foodName("Caesar Salad")
            .price(BigDecimal.valueOf(900))
            .rate(4.8)
            .store(store3)
            .currency(currency)
            .totalQuantity(90)
            .tags("healthy,salad,fresh")
            .build(),

        Food.builder()
            .foodName("Pepperoni Pizza")
            .price(BigDecimal.valueOf(1500))
            .rate(4.7)
            .store(store1)
            .currency(currency)
            .totalQuantity(60)
            .tags("pizza,cheesy,Italian")
            .build(),

        Food.builder()
            .foodName("Grilled Steak")
            .price(BigDecimal.valueOf(2500))
            .rate(4.9)
            .store(store2)
            .currency(currency)
            .totalQuantity(50)
            .tags("meat,grill,protein")
            .build(),

        Food.builder()
            .foodName("Tuna Sandwich")
            .price(BigDecimal.valueOf(800))
            .rate(3.9)
            .store(store3)
            .currency(currency)
            .totalQuantity(110)
            .tags("sandwich,fish,healthy")
            .build(),

        Food.builder()
            .foodName("Vegetable Stir Fry")
            .price(BigDecimal.valueOf(950))
            .rate(4.1)
            .store(store1)
            .currency(currency)
            .totalQuantity(85)
            .tags("vegetarian,stir-fry,Asian")
            .build(),

        Food.builder()
            .foodName("Beef Tacos")
            .price(BigDecimal.valueOf(1100))
            .rate(4.3)
            .store(store2)
            .currency(currency)
            .totalQuantity(70)
            .tags("Mexican,taco,spicy")
            .build(),

        Food.builder()
            .foodName("Chicken Noodles")
            .price(BigDecimal.valueOf(1050))
            .rate(4.2)
            .store(store3)
            .currency(currency)
            .totalQuantity(75)
            .tags("asian,noodles,chicken")
            .build(),

        Food.builder()
            .foodName("Margarita Pizza")
            .price(BigDecimal.valueOf(1400))
            .rate(4.6)
            .store(store1)
            .currency(currency)
            .totalQuantity(65)
            .tags("pizza,tomato,cheese")
            .build(),

        Food.builder()
            .foodName("Sushi Rolls")
            .price(BigDecimal.valueOf(1600))
            .rate(4.8)
            .store(store2)
            .currency(currency)
            .totalQuantity(55)
            .tags("sushi,fish,Japanese")
            .build(),

        Food.builder()
            .foodName("Pancakes with Maple Syrup")
            .price(BigDecimal.valueOf(700))
            .rate(4.4)
            .store(store3)
            .currency(currency)
            .totalQuantity(120)
            .tags("breakfast,sweet,fluffy")
            .build(),

        Food.builder()
            .foodName("Lasagna")
            .price(BigDecimal.valueOf(1300))
            .rate(4.7)
            .store(store1)
            .currency(currency)
            .totalQuantity(72)
            .tags("pasta,Italian,cheese")
            .build(),

        Food.builder()
            .foodName("Teriyaki Chicken")
            .price(BigDecimal.valueOf(1150))
            .rate(4.5)
            .store(store2)
            .currency(currency)
            .totalQuantity(80)
            .tags("Asian,sweet,chicken")
            .build(),

        Food.builder()
            .foodName("Salmon Fillet")
            .price(BigDecimal.valueOf(2000))
            .rate(4.9)
            .store(store3)
            .currency(currency)
            .totalQuantity(40)
            .tags("fish,protein,healthy")
            .build(),

        Food.builder()
            .foodName("Crispy French Fries")
            .price(BigDecimal.valueOf(600))
            .rate(3.8)
            .store(store1)
            .currency(currency)
            .totalQuantity(150)
            .tags("fast-food,potato,fried")
            .build(),

        Food.builder()
            .foodName("BBQ Ribs")
            .price(BigDecimal.valueOf(1800))
            .rate(4.7)
            .store(store2)
            .currency(currency)
            .totalQuantity(50)
            .tags("grill,meat,BBQ")
            .build(),

        Food.builder()
            .foodName("Falafel Wrap")
            .price(BigDecimal.valueOf(900))
            .rate(4.0)
            .store(store3)
            .currency(currency)
            .totalQuantity(95)
            .tags("vegan,wrap,Mediterranean")
            .build(),

        Food.builder()
            .foodName("Egg Fried Rice")
            .price(BigDecimal.valueOf(850))
            .rate(4.2)
            .store(store1)
            .currency(currency)
            .totalQuantity(105)
            .tags("rice,Asian,egg")
            .build(),

        Food.builder()
            .foodName("Chocolate Cake")
            .price(BigDecimal.valueOf(950))
            .rate(4.8)
            .store(store2)
            .currency(currency)
            .totalQuantity(65)
            .tags("dessert,chocolate,sweet")
            .build(),

        Food.builder()
            .foodName("Vanilla Ice Cream")
            .price(BigDecimal.valueOf(500))
            .rate(4.6)
            .store(store3)
            .currency(currency)
            .totalQuantity(130)
            .tags("dessert,ice-cream,cold")
            .build(),

        Food.builder()
            .foodName("Strawberry Cheesecake")
            .price(BigDecimal.valueOf(1100))
            .rate(4.7)
            .store(store1)
            .currency(currency)
            .totalQuantity(70)
            .tags("dessert,cake,strawberry")
            .build(),

        Food.builder()
            .foodName("Grilled Chicken Breast")
            .price(BigDecimal.valueOf(1350))
            .rate(4.5)
            .store(store2)
            .currency(currency)
            .totalQuantity(55)
            .tags("protein,chicken,healthy")
            .build(),

        Food.builder()
            .foodName("Mushroom Soup")
            .price(BigDecimal.valueOf(900))
            .rate(4.3)
            .store(store3)
            .currency(currency)
            .totalQuantity(95)
            .tags("soup,warm,healthy")
            .build(),

        Food.builder()
            .foodName("Butter Chicken")
            .price(BigDecimal.valueOf(1250))
            .rate(4.6)
            .store(store1)
            .currency(currency)
            .totalQuantity(85)
            .tags("Indian,spicy,curry")
            .build(),

        Food.builder()
            .foodName("Garlic Bread")
            .price(BigDecimal.valueOf(700))
            .rate(4.2)
            .store(store2)
            .currency(currency)
            .totalQuantity(120)
            .tags("bread,garlic,side-dish")
            .build(),

        Food.builder()
            .foodName("Asian Bread")
            .price(BigDecimal.valueOf(700))
            .rate(4.2)
            .store(store2)
            .currency(currency)
            .totalQuantity(120)
            .tags("bread,garlic,side-dish")
            .build(),

        Food.builder()
            .foodName("Garlic Bread")
            .price(BigDecimal.valueOf(700))
            .rate(4.2)
            .store(store4)
            .currency(currency)
            .totalQuantity(120)
            .tags("bread,garlic,side-dish")
            .build(),

        Food.builder()
            .foodName("Garlic Bread")
            .price(BigDecimal.valueOf(700))
            .rate(4.2)
            .store(store5)
            .currency(currency)
            .totalQuantity(120)
            .tags("bread,garlic,side-dish")
            .build()
    );
  }
}