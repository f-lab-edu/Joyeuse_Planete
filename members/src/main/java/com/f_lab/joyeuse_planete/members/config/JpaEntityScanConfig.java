package com.f_lab.joyeuse_planete.members.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionManager;

@Configuration
@EnableJpaRepositories(
    basePackages = {
        "com.f_lab.joyeuse_planete.members.repository",
        "com.f_lab.joyeuse_planete.core.kafka.repository",
        "com.f_lab.joyeuse_planete.core.domain.repository"
    })
@EntityScan(basePackages = { "com.f_lab.joyeuse_planete.core" })
public class JpaEntityScanConfig {

  @Primary
  @Bean(name = "transactionManager")
  public TransactionManager transactionManager() {
    return new JpaTransactionManager();
  }
}
