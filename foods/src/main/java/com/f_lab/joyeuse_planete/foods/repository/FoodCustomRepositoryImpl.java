package com.f_lab.joyeuse_planete.foods.repository;

import com.f_lab.joyeuse_planete.foods.domain.FoodSearchCondition;
import com.f_lab.joyeuse_planete.foods.dto.response.FoodDTO;
import com.f_lab.joyeuse_planete.foods.dto.response.QFoodDTO;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.f_lab.joyeuse_planete.core.domain.QCurrency.currency;
import static com.f_lab.joyeuse_planete.core.domain.QFood.food;
import static com.f_lab.joyeuse_planete.core.domain.QStore.store;


public class FoodCustomRepositoryImpl implements FoodCustomRepository {

  private final JPAQueryFactory queryFactory;
  private Map<String, OrderSpecifier> sortByMap = new HashMap<>();

  public FoodCustomRepositoryImpl(EntityManager em) {
    queryFactory = new JPAQueryFactory(em);
  }

  @PostConstruct
  public void init() {
    sortByMap.put("RATE_HIGH", food.rate.desc());
  }

  @Override
  public Page<FoodDTO> getFoodList(FoodSearchCondition condition, Pageable pageable) {
    List<FoodDTO> result = queryFactory
        .select(new QFoodDTO(
            food.id.as("foodId"),
            food.store.id.as("storeId"),
            food.currency.id.as("currencyId"),
            food.foodName.as("foodName"),
            food.price,
            food.totalQuantity.as("totalQuantity"),
            food.currency.currencyCode.as("currencyCode"),
            food.currency.currencySymbol.as("currencySymbol"),
            food.rate,
            food.collectionStartTime,
            food.collectionEndTime
        ))
        .from(food)
        .innerJoin(food.currency, currency)
        .innerJoin(food.store, store)
        .where(Expressions.anyOf(
            eqFoodName(condition.getSearch()),
            eqFoodTag(condition.getSearch()),
            eqStoreName(condition.getSearch()))
        )
        .orderBy(getOrders(condition.getSortBy()))
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    long count = queryFactory
        .select(food.count())
        .from(food)
        .fetch()
        .get(0);

    return new PageImpl<>(result, pageable, count);
  }

  private BooleanExpression eqFoodName(String search) {
    return (search != null) ? food.foodName.containsIgnoreCase(search) : null;
  }

  private BooleanExpression eqFoodTag(String search) {
    return (search != null) ? food.tags.containsIgnoreCase(search) : null;
  }

  private BooleanExpression eqStoreName(String search) {
    return (search != null) ? food.store.name.containsIgnoreCase(search) : null;
  }

  private OrderSpecifier[] getOrders(List<String> sortBy) {
    List<OrderSpecifier> list = new ArrayList<>();

    for (String sort : sortBy) {
      if (sortByMap.containsKey(sort))
        list.add(sortByMap.get(sort));
    }

    return list.toArray(OrderSpecifier[]::new);
  }
}
