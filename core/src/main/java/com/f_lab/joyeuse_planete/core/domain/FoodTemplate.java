package com.f_lab.joyeuse_planete.core.domain;

import com.f_lab.joyeuse_planete.core.domain.base.BaseEntity;
import com.f_lab.joyeuse_planete.core.domain.converter.StringListConverter;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "food_template")
public class FoodTemplate extends BaseEntity {

  @Id @GeneratedValue(strategy = IDENTITY)
  private Long id;

  private Long storeId;

  private String foodName;

  private BigDecimal price;

  private int totalQuantity;

  private String currencyCode;

  private String currencySymbol;

  @Convert(converter = StringListConverter.class)
  private List<String> tags;

  private LocalTime collectionStartTime;

  private LocalTime collectionEndTime;
}
