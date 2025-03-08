package com.f_lab.joyeuse_planete.core.kafka.aspect;

import com.f_lab.joyeuse_planete.core.annotation.Backoff;
import com.f_lab.joyeuse_planete.core.annotation.Retry;
import com.f_lab.joyeuse_planete.core.kafka.annotation.KafkaDeadLetterTopic;
import com.f_lab.joyeuse_planete.core.kafka.domain.DeadLetterTopic;
import com.f_lab.joyeuse_planete.core.kafka.repository.DeadLetterTopicRepository;
import com.f_lab.joyeuse_planete.core.util.log.LogUtil;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Aspect
@Component
@RequiredArgsConstructor
public class KafkaDeadLetterTopicAspect {

  private final DeadLetterTopicService deadLetterTopicService;
  private static final int MULTIPLIER = 3;

  @Around("@annotation(kafkaDeadLetterTopic)")
  public void saveDeadLetterTopic(ProceedingJoinPoint joinPoint, KafkaDeadLetterTopic kafkaDeadLetterTopic) {
    DeadLetterTopic deadLetterTopic = (DeadLetterTopic) joinPoint.getArgs()[0];

    try {
      deadLetterTopicService.updateDeadLetterTopicWithRetries(deadLetterTopic);
    } catch(Exception e) {
      LogUtil.exception("KafkaDeadLetterTopicAspect.saveDeadLetterTopic", e);
      LogUtil.deadLetterSaveFail(deadLetterTopic.getExceptionName(), deadLetterTopic.getExceptionMessage(), deadLetterTopic.getOriginalTopic());
    }
  }

  @Component
  @RequiredArgsConstructor
  public static class DeadLetterTopicService {

    private final DeadLetterTopicRepository deadLetterTopicRepository;

    @Transactional
    @Retry(backoff = @Backoff(multiplier = MULTIPLIER))
    public void updateDeadLetterTopicWithRetries(DeadLetterTopic deadLetterTopic) {
      DeadLetterTopic saveDeadLetterTopic = deadLetterTopicRepository.findById(deadLetterTopic.getId())
          .orElse(deadLetterTopic);

      saveDeadLetterTopic.setStatus(deadLetterTopic.getStatus());

      deadLetterTopicRepository.save(saveDeadLetterTopic);
    }
  }
}
