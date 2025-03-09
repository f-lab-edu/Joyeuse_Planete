package com.f_lab.joyeuse_planete.core.kafka.aspect;

import com.f_lab.joyeuse_planete.core.kafka.annotation.KafkaDeadLetterTopic;
import com.f_lab.joyeuse_planete.core.kafka.aspect.KafkaDeadLetterTopicAspect.DeadLetterTopicService;
import com.f_lab.joyeuse_planete.core.kafka.domain.DeadLetterStatus;
import com.f_lab.joyeuse_planete.core.kafka.domain.DeadLetterTopic;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;


@ExtendWith(MockitoExtension.class)
class KafkaUpdateStatusAspectTest {

  KafkaTestService proxy;

  @Mock
  DeadLetterTopicService deadLetterTopicService;

  @BeforeEach
  void beforeEach() {
    KafkaTestService service = spy(new KafkaTestService());
    AspectJProxyFactory factory = new AspectJProxyFactory(service);
    KafkaDeadLetterTopicAspect aspect = new KafkaDeadLetterTopicAspect(deadLetterTopicService);
    factory.addAspect(aspect);
    proxy = factory.getProxy();
  }

  @DisplayName("Kafka 데이터베이스 저장 확인")
  @Test
  void testKafkaAopWithDB() {
    // given
    DeadLetterTopic deadLetterTopic = createDeadLetterTopic();

    // when
    proxy.runKafkaDeadLetterTopic(deadLetterTopic);

    assertThat(deadLetterTopic.getStatus()).isEqualTo(DeadLetterStatus.PENDING);
  }

  @DisplayName("Kafka 토픽에 null 값이 포함되어 있어서 INVALID_FOR_REQUEUE 로 저장")
  @Test
  void testKafkaAopWithInValidNullValueFail() {
    // given
    DeadLetterTopic deadLetterTopic = createInvalidDeadLetterTopic();

    assertThat(deadLetterTopic.getStatus()).isEqualTo(DeadLetterStatus.INVALID_FOR_REQUEUE);
  }

  DeadLetterTopic createDeadLetterTopic() {
    return DeadLetterTopic.createInstance(
        "test",
        "test",
        "testId",
        "testException",
        "testExceptionMessage",
        "testExceptionStackTrace",
        "testExceptionOriginalTopic"
    );
  }

  DeadLetterTopic createInvalidDeadLetterTopic() {
    return DeadLetterTopic.createInstance(
        "test",
        null,
        "testId",
        "testException",
        "testExceptionMessage",
        "testExceptionStackTrace",
        "testExceptionOriginalTopic"
    );
  }

  @Slf4j
  static class KafkaTestService {

    @KafkaDeadLetterTopic
    public void runKafkaDeadLetterTopic(DeadLetterTopic deadLetterTopic) {
      log.info("테스트 카프카 로직");
    }
  }
}