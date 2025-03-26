package com.f_lab.joyeuse_planete.foods.service;


import com.f_lab.joyeuse_planete.core.domain.FoodOrderReserve;
import com.f_lab.joyeuse_planete.core.events.OrderCancelEvent;
import com.f_lab.joyeuse_planete.core.events.OrderCreatedEvent;
import com.f_lab.joyeuse_planete.foods.exceptions.AlreadyProcessedEventException;
import com.f_lab.joyeuse_planete.foods.repository.FoodOrderReleaseRepository;
import com.f_lab.joyeuse_planete.foods.repository.FoodOrderReserveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EventDuplicateCheckService {

  private final FoodOrderReserveRepository foodOrderReserveRepository;
  private final FoodOrderReleaseRepository foodOrderReleaseRepository;


  @Transactional
  public void writeReserveEventLog(OrderCreatedEvent orderCreatedEvent) throws AlreadyProcessedEventException {
    try {
      foodOrderReserveRepository.insert(
          orderCreatedEvent.getFoodId(),
          orderCreatedEvent.getOrderId());

      // 중복 키 감지시 발생
    } catch (DataIntegrityViolationException e) {
      throw new AlreadyProcessedEventException(e);
    }
  }

  @Transactional
  public void writeReleaseEventLog(OrderCancelEvent orderCancelEvent) throws AlreadyProcessedEventException {
    try {
      foodOrderReleaseRepository.insert(
          orderCancelEvent.getFoodId(),
          orderCancelEvent.getOrderId());

      // 중복 키 감지시 발생
    } catch (DataIntegrityViolationException e) {
      throw new AlreadyProcessedEventException(e);
    }
  }
}
