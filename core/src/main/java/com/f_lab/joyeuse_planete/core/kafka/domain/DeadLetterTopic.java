package com.f_lab.joyeuse_planete.core.kafka.domain;

import com.f_lab.joyeuse_planete.core.domain.base.BaseTimeEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.domain.Persistable;
import org.springframework.util.ObjectUtils;

import static jakarta.persistence.EnumType.STRING;

@Entity
@Builder
@ToString
@DynamicInsert
@DynamicUpdate
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "dead_letter_topics")
public class DeadLetterTopic extends BaseTimeEntity implements Persistable<String> {

  @Id
  private String id;

  @JsonIgnore
  private transient Object event;

  @JsonIgnore
  private static String DEFAULT_NULL_MESSAGE = "null";

  @Column(name = "event", columnDefinition = "VARCHAR(50)")
  private String eventName;

  @Column(name = "exception_name", columnDefinition = "VARCHAR(1000)")
  private String exceptionName;

  @Column(name = "exception_message", columnDefinition = "VARCHAR(1000)")
  private String exceptionMessage;

  @Column(name = "exception_stacktrace", columnDefinition = "MEDIUMTEXT")
  private String exceptionStackTrace;

  @Column(name = "original_topic", columnDefinition = "VARCHAR(1000)")
  private String originalTopic;

  @Enumerated(STRING)
  @Column(name = "status")
  private DeadLetterStatus status;

  @Override
  public boolean isNew() {
    return super.getCreatedAt() == null;
  }

  public static DeadLetterTopic createInstance(
      Object event,
      String eventName,
      String topicId,
      String exceptionName,
      String exceptionMessage,
      String exceptionStackTrace,
      String originalTopic
  ) {
    return (validateParams(event, eventName, topicId, exceptionName, exceptionMessage, exceptionStackTrace, originalTopic))
        ? defaultInvalidTopic(
            event,
        eventName,
        topicId,
        exceptionName,
        exceptionMessage,
        exceptionStackTrace,
        originalTopic
    )

        : DeadLetterTopic.builder()
        .id(topicId)
        .event(event)
        .eventName(eventName)
        .exceptionName(exceptionName)
        .exceptionMessage(exceptionMessage)
        .exceptionStackTrace(exceptionStackTrace)
        .originalTopic(originalTopic)
        .status(DeadLetterStatus.PENDING)
        .build();
  }

  private static boolean validateParams(Object... params) {
    for (Object param : params) {
      if (ObjectUtils.isEmpty(param))
        return false;
    }

    return true;
  }

  private static DeadLetterTopic defaultInvalidTopic(
      Object event,
      String eventName,
      String topicId,
      String exceptionName,
      String exceptionMessage,
      String exceptionStackTrace,
      String originalTopic
  ) {
    return DeadLetterTopic.builder()
        .id(defaultIfNull(topicId))
        .event(event)
        .eventName(defaultIfNull(eventName))
        .exceptionName(defaultIfNull(exceptionName))
        .exceptionMessage(defaultIfNull(exceptionMessage))
        .exceptionStackTrace(defaultIfNull(exceptionStackTrace))
        .originalTopic(defaultIfNull(originalTopic))
        .status(DeadLetterStatus.FAILED_INVALID_FOR_REQUEUE)
        .build();
  }

  private static String defaultIfNull(String value) {
    return (value == null || value.trim().isEmpty()) ? DEFAULT_NULL_MESSAGE : value;
  }
}
