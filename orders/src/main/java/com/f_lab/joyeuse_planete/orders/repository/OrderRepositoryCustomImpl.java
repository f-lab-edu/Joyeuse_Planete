package com.f_lab.joyeuse_planete.orders.repository;

import com.f_lab.joyeuse_planete.core.domain.OrderStatus;
import com.f_lab.joyeuse_planete.orders.domain.OrderSearchCondition;
import com.f_lab.joyeuse_planete.orders.dto.response.OrderDTO;
import com.f_lab.joyeuse_planete.orders.dto.response.QOrderDTO;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.f_lab.joyeuse_planete.core.domain.QFood.food;
import static com.f_lab.joyeuse_planete.core.domain.QOrder.order;
import static com.f_lab.joyeuse_planete.core.domain.QPayment.payment;


public class OrderRepositoryCustomImpl implements OrderRepositoryCustom {

  private Map<String, OrderSpecifier> sortByMap = new HashMap<>();
  private static final List<String> defaultSortBy = List.of("DATE_NEW");

  @PostConstruct
  void init() {
    sortByMap.put("PRICE_LOW", order.totalCost.asc());
    sortByMap.put("PRICE_HIGH", order.totalCost.desc());
    sortByMap.put("DATE_NEW", order.createdAt.desc());
    sortByMap.put("DATE_OLD", order.createdAt.asc());
  }

  private final JPAQueryFactory queryFactory;

  public OrderRepositoryCustomImpl(EntityManager em) {
    this.queryFactory = new JPAQueryFactory(em);
  }

  @Override
  public Page<OrderDTO> findOrders(OrderSearchCondition condition, Pageable pageable) {
    List<OrderDTO> results = queryFactory
        .select(new QOrderDTO(
            order.id.as("orderId"),
            food.foodName,
            order.totalCost,
            food.currency.currencyCode,
            food.currency.currencySymbol,
            order.quantity,
            order.status.stringValue(),
            order.payment.id.as("paymentId"),
            order.voucher.id.as("voucherId"),
            order.collectionTime
        ))
        .from(order)
        .leftJoin(order.payment, payment)
        .leftJoin(order.food, food)
        .where(
            eqStatus(condition.getStatus()),
            dateGoe(condition.getStartDate()),
            dateLoe(condition.getEndDate()),
            totalCostGoe(condition.getMinCost()),
            totalCostLoe(condition.getMaxCost())
        )
        .orderBy(getOrders(condition.getSortBy()))
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    long count = queryFactory
        .select(order.count())
        .from(order)
        .where(
            eqStatus(condition.getStatus()),
            dateGoe(condition.getStartDate()),
            dateLoe(condition.getEndDate()),
            totalCostGoe(condition.getMinCost()),
            totalCostLoe(condition.getMaxCost())
        )
        .fetch()
        .get(0);

    return new PageImpl<>(results, pageable, count);
  }

  private BooleanExpression eqStatus(String status) {
    return (status != null) ? order.status.eq(OrderStatus.valueOf(status)) : null;
  }

  private BooleanExpression dateGoe(LocalDateTime date) {
    return date != null ? order.createdAt.goe(date) : null;
  }

  private BooleanExpression dateLoe(LocalDateTime date) {
    return date != null ? order.createdAt.loe(date) : null;
  }

  private BooleanExpression totalCostGoe(BigDecimal cost) {
    return cost != null ? order.totalCost.goe(cost) : null;
  }

  private BooleanExpression totalCostLoe(BigDecimal cost) {
    return cost != null ? order.totalCost.loe(cost) : null;
  }

  private OrderSpecifier[] getOrders(List<String> sortBy) {
    List<OrderSpecifier> list = new ArrayList<>();

    if (sortBy == null)
      sortBy = defaultSortBy;

    for (String sort : sortBy) {
      if (sortByMap.containsKey(sort))
        list.add(sortByMap.get(sort));
    }

    return list.toArray(OrderSpecifier[]::new);
  }
}
