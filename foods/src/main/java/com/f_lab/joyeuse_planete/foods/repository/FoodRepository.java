package com.f_lab.joyeuse_planete.foods.repository;

import com.f_lab.joyeuse_planete.core.annotation.Backoff;
import com.f_lab.joyeuse_planete.core.annotation.Retry;
import com.f_lab.joyeuse_planete.core.domain.Food;
import jakarta.persistence.LockModeType;
import jakarta.persistence.LockTimeoutException;
import jakarta.persistence.PessimisticLockException;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

import static com.f_lab.joyeuse_planete.core.util.time.TimeConstants.TimeConstantsString.FIVE_SECONDS;


public interface FoodRepository extends JpaRepository<Food, Long>, FoodCustomRepository {

  @Retry(retryable = { LockTimeoutException.class, PessimisticLockException.class }, backoff = @Backoff(multiplier = 2))
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @EntityGraph(attributePaths = { "store" })
  @Query("SELECT f FROM Food f WHERE f.id = :id")
  @QueryHints({ @QueryHint(name = "jakarta.persistence.lock.timeout", value = FIVE_SECONDS) })
  Optional<Food> findFoodByFoodIdWithPessimisticLock(@Param("id") Long id);

  @Override
  @EntityGraph(attributePaths = { "store" })
  Optional<Food> findById(@Param("foodId") Long foodId);
}

