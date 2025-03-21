package com.f_lab.joyeuse_planete.core.events;

import com.f_lab.joyeuse_planete.core.exceptions.ErrorCode;

public class EventToEventMapper {

  public static FoodReservationFailedEvent mapToCompensationEvent(OrderCreatedEvent event, ErrorCode errorCode) {
    return FoodReservationFailedEvent.toEvent(event, errorCode);
  }

  public static FoodReservationFailedEvent mapToCompensationEvent(FoodReservationProcessedEvent event, ErrorCode errorCode) {
    return FoodReservationFailedEvent.toEvent(event, errorCode);
  }

  public static FoodReleaseFailedEvent mapToCompensationEvent(OrderCancelEvent event, ErrorCode errorCode) {
    return FoodReleaseFailedEvent.toEvent(event, errorCode);
  }

  public static FoodReleaseFailedEvent mapToCompensationEvent(FoodReleaseEvent event, ErrorCode errorCode) {
    return FoodReleaseFailedEvent.toEvent(event, errorCode);
  }
}
