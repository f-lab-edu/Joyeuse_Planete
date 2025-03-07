package com.f_lab.joyeuse_planete.core.domain;


import com.f_lab.joyeuse_planete.core.domain.base.BaseEntity;
import com.f_lab.joyeuse_planete.core.domain.util.CurrencyMap;
import com.f_lab.joyeuse_planete.core.util.time.TimeConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

import static com.f_lab.joyeuse_planete.core.util.time.TimeConstants.TimeConstantsString.THIRTY_MINUTES;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@SuperBuilder
@DynamicInsert
@DynamicUpdate
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@Table(name = "orders")
public class Order extends BaseEntity {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  @Column(updatable = false)
  private Long foodId;

  private String foodName;

  @Column(name = "collection_start_time")
  private LocalTime collectionStartTime;

  @Column(name = "collection_end_time")
  private LocalTime collectionEndTime;

  @Column(updatable = false)
  private Long storeId;

  private String storeName;

  private BigDecimal totalCost;

  private String currencyCode;

  private String currencySymbol;

  private int quantity;

  private BigDecimal rate;

  @Column(columnDefinition = "BOOLEAN NOT NULL DEFAULT 'FALSE'")
  private boolean isRated;

  @Enumerated(EnumType.STRING)
  private OrderStatus status;

  @OneToOne(mappedBy = "order", fetch = LAZY)
  private Payment payment;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "voucher_id")
  private Voucher voucher;

  public boolean isCancellable() {
    return collectionStartTime.isAfter(
        LocalTime.now().plusMinutes(TimeUnit.MILLISECONDS.toMinutes(TimeConstants.TimeConstantsMillis.THIRTY_MINUTES)));
  }
}
