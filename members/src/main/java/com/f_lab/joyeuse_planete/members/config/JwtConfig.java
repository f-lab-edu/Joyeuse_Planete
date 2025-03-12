package com.f_lab.joyeuse_planete.members.config;

import com.f_lab.joyeuse_planete.core.util.jwt.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

  @Bean
  public JwtUtil jwtUtil() {
    return new JwtUtil(new ObjectMapper());
  }
}
