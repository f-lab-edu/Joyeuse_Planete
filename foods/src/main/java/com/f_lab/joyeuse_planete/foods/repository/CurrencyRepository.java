package com.f_lab.joyeuse_planete.foods.repository;

import com.f_lab.joyeuse_planete.core.domain.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CurrencyRepository extends JpaRepository<Currency, Long> {

  Optional<Currency> findByCurrencyCode(String currencyCode);
}
