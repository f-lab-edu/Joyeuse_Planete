package com.f_lab.joyeuse_planete.payment.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
    basePackages = {
        "com.f_lab.joyeuse_planete.payment.repository",
        "com.f_lab.joyeuse_planete.core.kafka.repository"
    }
)
@EntityScan(basePackages = { "com.f_lab.joyeuse_planete.core" })
public class JpaEntityScanConfig {
}
