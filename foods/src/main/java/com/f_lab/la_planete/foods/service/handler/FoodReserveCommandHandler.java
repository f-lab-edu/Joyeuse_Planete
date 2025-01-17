package com.f_lab.la_planete.foods.service.handler;

import com.f_lab.la_planete.core.commands.FoodReserveCommand;
import com.f_lab.la_planete.core.commands.PaymentProcessCommand;
import com.f_lab.la_planete.core.domain.Food;
import com.f_lab.la_planete.core.events.FoodReserveFailEvent;
import com.f_lab.la_planete.foods.service.FoodService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
@KafkaListener(
    topics = { "${foods.commands.topic.name}" },
    groupId = "${spring.kafka.consumer.group-id}"
)
public class FoodReserveCommandHandler {


  private final KafkaTemplate<String, Object> kafkaTemplate;
  private final FoodService foodService;

  @Value("${payment.commands.topic.name}")
  private String paymentProcessCommand;

  @Value("${foods.events.topics.name.fail}")
  private String foodProcessFailEvent;

  @KafkaHandler
  public void handleFoodReserveCommand(@Payload FoodReserveCommand foodReserveCommand) {
    log.info("foodReserveCommand={}", foodReserveCommand);

    try {
      foodService.reserve(
          foodReserveCommand.getFoodId(),
          foodReserveCommand.getQuantity());

      kafkaTemplate.send(
          paymentProcessCommand,
          PaymentProcessCommand.toCommand(foodReserveCommand));

    } catch(Exception e) {
       kafkaTemplate.send(foodProcessFailEvent, new FoodReserveFailEvent());
       throw new FoodServiceHandlerFailException(e);
    }
  }

  static class FoodServiceHandlerFailException extends RuntimeException {
    public FoodServiceHandlerFailException() {
    }

    public FoodServiceHandlerFailException(String message) {
      super(message);
    }

    public FoodServiceHandlerFailException(String message, Throwable cause) {
      super(message, cause);
    }

    public FoodServiceHandlerFailException(Throwable cause) {
      super(cause);
    }

    public FoodServiceHandlerFailException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
      super(message, cause, enableSuppression, writableStackTrace);
    }
  }
}
