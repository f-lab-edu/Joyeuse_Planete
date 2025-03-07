package com.f_lab.joyeuse_planete.core.kafka.repository;


import com.f_lab.joyeuse_planete.core.kafka.domain.DeadLetterTopic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeadLetterTopicRepository extends JpaRepository<DeadLetterTopic, String> {

}
