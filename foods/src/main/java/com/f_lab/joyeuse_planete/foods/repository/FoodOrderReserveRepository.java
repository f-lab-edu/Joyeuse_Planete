package com.f_lab.joyeuse_planete.foods.repository;

import com.f_lab.joyeuse_planete.core.domain.FoodOrderReserve;
import com.f_lab.joyeuse_planete.core.domain.ids.FoodOrderId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FoodOrderReserveRepository extends JpaRepository<FoodOrderReserve, FoodOrderId> {

  @Modifying
  @Query(value = "INSERT INTO food_order_reserves (food_id, order_id) VALUES (:foodId, :orderId)", nativeQuery = true)
  void insert(@Param("foodId") Long foodId, @Param("orderId") Long orderId);
}
