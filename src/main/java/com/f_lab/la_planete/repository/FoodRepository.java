package com.f_lab.la_planete.repository;

import com.f_lab.la_planete.domain.Food;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

public interface FoodRepository extends JpaRepository<Food, Long> {

  String FIVE_SECONDS = "1000";

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT f FROM Food f WHERE f.id = :id")
  @QueryHints({ @QueryHint(name = "jakarta.persistence.lock.timeout", value = FIVE_SECONDS) })
  Food findFoodByFoodIdWithPessimisticLock(@Param("id") Long id);
}
