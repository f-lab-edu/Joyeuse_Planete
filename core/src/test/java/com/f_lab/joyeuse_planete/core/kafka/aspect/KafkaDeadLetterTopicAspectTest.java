package com.f_lab.joyeuse_planete.core.kafka.aspect;

import com.f_lab.joyeuse_planete.core.aspect.RetryAspect;
import com.f_lab.joyeuse_planete.core.kafka.annotation.KafkaDeadLetterTopic;
import com.f_lab.joyeuse_planete.core.kafka.aspect.KafkaDeadLetterTopicAspect.DeadLetterTopicService;
import com.f_lab.joyeuse_planete.core.kafka.domain.DeadLetterStatus;
import com.f_lab.joyeuse_planete.core.kafka.domain.DeadLetterTopic;
import com.f_lab.joyeuse_planete.core.kafka.repository.DeadLetterTopicRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class KafkaDeadLetterTopicAspectTest {

  KafkaTestService proxy;

  @Mock
  DeadLetterTopicRepository deadLetterTopicRepository;

  @BeforeEach
  void beforeEach() {
    KafkaTestService service = spy(new KafkaTestService());
    DeadLetterTopicService deadLetterService = spy(new DeadLetterTopicService(deadLetterTopicRepository));

    AspectJProxyFactory factory = new AspectJProxyFactory(service);
    AspectJProxyFactory deadLetterFactory = new AspectJProxyFactory(deadLetterService);

    deadLetterFactory.addAspect(new RetryAspect());
    KafkaDeadLetterTopicAspect aspect = new KafkaDeadLetterTopicAspect(deadLetterFactory.getProxy());
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

    // then
    assertThat(deadLetterTopic.getStatus()).isEqualTo(DeadLetterStatus.PENDING);
    verify(deadLetterTopicRepository, times(1)).save(any());
  }

  @DisplayName("Kafka 토픽에 null 값이 포함되어 있어서 INVALID_FOR_REQUEUE 로 저장")
  @Test
  void testKafkaAopWithInValidNullValueSuccess() {
    // given
    DeadLetterTopic deadLetterTopic = createInvalidDeadLetterTopic();

    // when
    proxy.runKafkaDeadLetterTopic(deadLetterTopic);

    // then
    assertThat(deadLetterTopic.getStatus()).isEqualTo(DeadLetterStatus.INVALID_FOR_REQUEUE);
    verify(deadLetterTopicRepository, times(1)).save(any());
  }

  @DisplayName("DB 로직이 실패할 경우 retry 후 실패 성공")
  @Test
  void testKafkaAopWithDBRetrySuccess() {
    // given
    DeadLetterTopic deadLetterTopic = createInvalidDeadLetterTopic();

    // when
    when(deadLetterTopicRepository.save(any()))
        .thenThrow(new RuntimeException("오류 발생1"))
        .thenThrow(new RuntimeException("오류 발생2"))
        .thenReturn(null);

    proxy.runKafkaDeadLetterTopic(deadLetterTopic);

    // then
    assertThat(deadLetterTopic.getStatus()).isEqualTo(DeadLetterStatus.INVALID_FOR_REQUEUE);
    verify(deadLetterTopicRepository, times(3)).save(any());
  }

  @DisplayName("DB 로직이 실패할 경우 retry 후 실패 처리")
  @Test
  void testKafkaAopWithDBRetryFail() {
    // given
    DeadLetterTopic deadLetterTopic = createInvalidDeadLetterTopic();

    // when
    when(deadLetterTopicRepository.save(any()))
        .thenThrow(new RuntimeException("오류 발생1"))
        .thenThrow(new RuntimeException("오류 발생2"))
        .thenThrow(new RuntimeException("오류 발생3"));

    proxy.runKafkaDeadLetterTopic(deadLetterTopic);

    // then
    assertThat(deadLetterTopic.getStatus()).isEqualTo(DeadLetterStatus.INVALID_FOR_REQUEUE);
    verify(deadLetterTopicRepository, times(3)).save(any());
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