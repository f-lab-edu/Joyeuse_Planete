package com.f_lab.la_planete.service;

import com.f_lab.la_planete.domain.Food;
import com.f_lab.la_planete.domain.Order;
import com.f_lab.la_planete.domain.Payment;
import com.f_lab.la_planete.dto.request.OrderCreateRequestDTO;
import com.f_lab.la_planete.dto.response.OrderCreateResponseDTO;
import com.f_lab.la_planete.repository.FoodRepository;
import com.f_lab.la_planete.repository.OrderRepository;
import com.f_lab.la_planete.repository.PaymentRepository;
import jakarta.persistence.LockTimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.PessimisticLockException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

  private final OrderRepository orderRepository;
  private final FoodRepository foodRepository;
  private final PaymentRepository paymentRepository;

  @Transactional
  public OrderCreateResponseDTO createFoodOrder(OrderCreateRequestDTO request) {

    // 음식을 조회 후 요청한 수 만큼 빼기
    Food food = findFoodWithLock(request.getFoodId());
    food.minusQuantity(request.getQuantity());
    foodRepository.save(food);

    // 주문 생성 후 총 금액 계산
    Order order = request.toEntity(food);
    BigDecimal totalCost = order.calculateTotalCost();
    order.updateTotalCost(totalCost);

    // 결제 생성
    Payment payment = Payment.of(totalCost, order);

    orderRepository.save(order);
    paymentRepository.save(payment);

    return new OrderCreateResponseDTO("CREATED");
  }

  private Food findFoodWithLock(Long foodId) {
     return foodRepository.findFoodByFoodIdWithPessimisticLock(foodId);
  }
}

