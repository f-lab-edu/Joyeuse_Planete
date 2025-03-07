package com.f_lab.joyeuse_planete.core.events;

import com.f_lab.joyeuse_planete.core.exceptions.ErrorCode;

public class EventToEventMapper {

  public static FoodReservationFailedEvent mapToCompensationEvent(OrderCreatedEvent orderCreatedEvent, ErrorCode errorCode) {
    return FoodReservationFailedEvent.toEvent(orderCreatedEvent, errorCode);
  }

  public static FoodReleaseFailedEvent mapToCompensationEvent(OrderCancelEvent orderCancelEvent, ErrorCode errorCode) {
    return FoodReleaseFailedEvent.toEvent(orderCancelEvent, errorCode);
  }
}
