package com.f_lab.joyeuse_planete.foods.code;

import org.junit.ClassRule;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public class TestContainers {

  @ClassRule
  @Container
  public static GenericContainer redis = new GenericContainer(DockerImageName.parse("redis:6-alpine"))
      .withExposedPorts(6379);

  @DynamicPropertySource
  public static void overrideRedisProps(DynamicPropertyRegistry registry) {
    registry.add("spring.redis.host", redis::getHost);
    registry.add("spring.redis.port", () -> redis.getMappedPort(6379));
  }
}
