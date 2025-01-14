package com.f_lab.la_planete.notifications.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EntityScan(basePackages = { "com.f_lab.la_planete.core" })
public class JpaEntityScanConfig {
}
