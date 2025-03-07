package com.f_lab.joyeuse_planete.core.domain;

import com.f_lab.joyeuse_planete.core.domain.base.BaseEntity;
import com.f_lab.joyeuse_planete.core.domain.converter.StringListConverter;
import com.f_lab.joyeuse_planete.core.exceptions.ErrorCode;
import com.f_lab.joyeuse_planete.core.exceptions.JoyeusePlaneteApplicationException;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;


@Entity
@Builder
@DynamicInsert
@DynamicUpdate
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "foods")
@SQLRestriction("is_deleted IS FALSE")
@SQLDelete(sql = "UPDATE foods SET is_deleted = true WHERE id = ?")
public class Food extends BaseEntity {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  private String foodName;

  private BigDecimal price;

  private int totalQuantity;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "store_id")
  private Store store;

  private String currencyCode;

  private String currencySymbol;

  private BigDecimal rate;

  @Convert(converter = StringListConverter.class)
  private List<String> tags;

  @Convert(converter = StringListConverter.class)
  private List<String> searchTags;

  private LocalTime collectionStartTime;

  private LocalTime collectionEndTime;

  public BigDecimal calculateCost(int quantity) {
    return price.multiply(BigDecimal.valueOf(quantity));
  }

  public void minusQuantity(int quantity) {
    if (totalQuantity - quantity < 0)
      throw new JoyeusePlaneteApplicationException(ErrorCode.FOOD_NOT_ENOUGH_STOCK);

    totalQuantity -= quantity;
  }

  public void plusQuantity(int quantity) {
    if (totalQuantity > Integer.MAX_VALUE - quantity)
      throw new JoyeusePlaneteApplicationException(ErrorCode.FOOD_QUANTITY_OVERFLOW);

    totalQuantity += quantity;
  }

  public void update(
      String foodName,
      BigDecimal price,
      int totalQuantity,
      LocalTime collectionStartTime,
      LocalTime collectionEndTime
  ) {

    this.foodName = foodName;
    this.price = price;
    this.totalQuantity = totalQuantity;
    this.collectionStartTime = collectionStartTime;
    this.collectionEndTime = collectionEndTime;
  }
}