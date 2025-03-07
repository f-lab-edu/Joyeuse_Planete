package com.f_lab.joyeuse_planete.orders.repository;

import com.f_lab.joyeuse_planete.core.domain.OrderStatus;
import com.f_lab.joyeuse_planete.orders.dto.request.OrderSearchCondition;
import com.f_lab.joyeuse_planete.orders.dto.response.OrderDTO;
import com.f_lab.joyeuse_planete.orders.dto.response.QOrderDTO;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static com.f_lab.joyeuse_planete.core.domain.QOrder.order;
import static com.f_lab.joyeuse_planete.core.domain.QPayment.payment;

public class OrderRepositoryCustomImpl implements OrderRepositoryCustom {

  private static final Map<String, OrderSpecifier> sortByMap = Map.of(
      "PRICE_LOW", order.totalCost.asc(),
      "PRICE_HIGH", order.totalCost.desc(),
      "DATE_NEW", order.createdAt.desc(),
      "DATE_OLD", order.createdAt.asc()
  );

  private final JPAQueryFactory queryFactory;

  public OrderRepositoryCustomImpl(EntityManager em) {
    this.queryFactory = new JPAQueryFactory(em);
  }

  @Override
  public OrderDTO getOrder(Long orderId) {
    return queryFactory.select(
        new QOrderDTO(
            order.id.as("orderId"),
            order.foodName,
            order.totalCost,
            order.currencyCode,
            order.currencySymbol,
            order.quantity,
            order.rate,
            order.status.stringValue(),
            order.payment.id.as("paymentId"),
            order.voucher.id.as("voucherId"),
            order.createdAt.as("createdAt")
        ))
        .from(order)
        .leftJoin(order.payment, payment)
        .where(eqOrderId(orderId))
        .fetchOne();
  }

  @Override
  public Page<OrderDTO> findOrders(OrderSearchCondition condition, Pageable pageable) {
    List<OrderDTO> results = queryFactory
        .select(new QOrderDTO(
            order.id.as("orderId"),
            order.foodName,
            order.totalCost,
            order.currencyCode,
            order.currencySymbol,
            order.quantity,
            order.rate,
            order.status.stringValue(),
            order.payment.id.as("paymentId"),
            order.voucher.id.as("voucherId"),
            order.createdAt.as("createdAt")
        ))
        .from(order)
        .leftJoin(order.payment, payment)
        .where(
            eqStatus(condition.getStatus()),
            dateGoe(condition.getStartDate()),
            dateLoe(condition.getEndDate()),
            totalCostGoe(condition.getMinCost()),
            totalCostLoe(condition.getMaxCost())
        )
        .orderBy(getOrderSpecifiers(condition.getSortBy()))
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

  private BooleanExpression eqOrderId(Long orderId) {
    return order.id.eq(orderId);
  }

  private OrderSpecifier[] getOrderSpecifiers(List<String> sortBy) {
    OrderSpecifier[] specifiedOrders = sortBy.stream()
        .filter(sortByMap::containsKey)
        .map(sortByMap::get)
        .toArray(OrderSpecifier[]::new);

    return specifiedOrders.length > 0
        ? specifiedOrders
        : new OrderSpecifier[]{ order.createdAt.desc() };
  }
}
