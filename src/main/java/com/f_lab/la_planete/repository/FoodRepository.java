package com.f_lab.la_planete.repository;

import com.f_lab.la_planete.domain.Food;
import org.springframework.data.jpa.repository.JpaRepository;


public interface FoodRepository extends JpaRepository<Food, Long> {
}