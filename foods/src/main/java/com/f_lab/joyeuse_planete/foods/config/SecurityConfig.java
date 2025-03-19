package com.f_lab.joyeuse_planete.foods.config;

import com.f_lab.joyeuse_planete.core.domain.repository.RefreshTokenRepository;
import com.f_lab.joyeuse_planete.core.security.config.DefaultSecurityConfig;
import com.f_lab.joyeuse_planete.core.security.cookie.CookieUtil;
import com.f_lab.joyeuse_planete.core.security.filter.JwtExceptionFilter;
import com.f_lab.joyeuse_planete.core.security.filter.JwtFilter;
import com.f_lab.joyeuse_planete.core.security.jwt.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityConfig extends DefaultSecurityConfig {

  @Autowired
  private RefreshTokenRepository refreshTokenRepository;

  @Override
  protected Filter jwtFilter() {
    return new JwtFilter(jwtUtil(), cookieUtil(), refreshTokenRepository);
  }

  @Override
  protected Filter jwtExceptionFilter() {
    return new JwtExceptionFilter();
  }

  @Bean
  public JwtUtil jwtUtil() {
    return new JwtUtil(new ObjectMapper());
  }

  @Bean
  public CookieUtil cookieUtil() {
    return new CookieUtil();
  }
}
