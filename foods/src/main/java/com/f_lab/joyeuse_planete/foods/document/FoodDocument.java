package com.f_lab.joyeuse_planete.foods.document;

import jakarta.persistence.Id;
import lombok.Data;
import lombok.Getter;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(indexName = "foods")
public class FoodDocument {

  @Id
  private Long id;

  @Field(name = "store_id", type = FieldType.Long)
  private Long storeId;

  @Field(name = "food_name", type = FieldType.Text)
  private String foodName;

  @Field(name = "price", type = FieldType.Scaled_Float, scalingFactor = 1000)
  private BigDecimal price;

  @Field(name = "total_quantity", type = FieldType.Integer)
  private int totalQuantity;

  @Field(name = "currency_code", type = FieldType.Keyword)
  private String currencyCode;

  @Field(name = "currency_symbol", type = FieldType.Keyword)
  private String currencySymbol;

  @Field(name = "tags", type = FieldType.Text)
  private String tags;

  @Field(name = "rate", type = FieldType.Scaled_Float, scalingFactor = 1000)
  private BigDecimal rate;

  @Field(name = "collection_start_time", type = FieldType.Date, format = DateFormat.date_optional_time)
  private Instant collectionStartTime;

  @Field(name = "collection_end_time", type = FieldType.Date, format = DateFormat.date_optional_time)
  private Instant collectionEndTime;

  @Field(name = "created_at", type = FieldType.Date, format = DateFormat.date_optional_time)
  private Instant createdAt;

  @Field(name = "modified_at", type = FieldType.Date, format = DateFormat.date_optional_time)
  private Instant modifiedAt;
}

