package com.f_lab.la_planete.core.events;


import com.f_lab.la_planete.core.commands.FoodReserveCommand;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoodReserveFailEvent {

  private Long foodId;

  private Long storeId;

  private int quantity;

  public static FoodReserveFailEvent toEvent(FoodReserveCommand foodReserveCommand) {
    return FoodReserveFailEvent.builder()
        .foodId(foodReserveCommand.getFoodId())
        .storeId(foodReserveCommand.getStoreId())
        .quantity(foodReserveCommand.getQuantity())
        .build();
  }
}
