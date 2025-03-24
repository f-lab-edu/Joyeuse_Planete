package com.f_lab.joyeuse_planete.core.domain;

import com.f_lab.joyeuse_planete.core.domain.base.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;


import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "store_food_auto_registers")
public class StoreFoodAutoRegister extends BaseEntity {

  @Id @GeneratedValue(strategy = IDENTITY)
  private Long id;

  @OneToOne(fetch = LAZY)
  @JoinColumn(name = "food_template_id")
  private FoodTemplate foodTemplate;
}
