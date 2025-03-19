package com.f_lab.joyeuse_planete.core.security.config;


import com.f_lab.joyeuse_planete.core.security.filter.JwtFilter;
import jakarta.servlet.Filter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static com.f_lab.joyeuse_planete.core.util.time.TimeConstants.TimeConstantLong.ONE_HOUR;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public abstract class DefaultSecurityConfig {

  @Value("${cookie.domain:.localhost}")
  private String DOMAIN;

  abstract protected Filter jwtFilter();
  abstract protected Filter jwtExceptionFilter();


  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
    return http

        .csrf(AbstractHttpConfigurer::disable)

        .cors(cors -> cors.
            configurationSource(corsConfigurationSource()))

        .formLogin(AbstractHttpConfigurer::disable)

        .httpBasic(AbstractHttpConfigurer::disable)

        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

        .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(jwtExceptionFilter(), JwtFilter.class)

        .build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    configuration.addAllowedOrigin(DOMAIN);
    configuration.addAllowedMethod("*");
    configuration.addAllowedHeader("*");
    configuration.setAllowCredentials(true);
    configuration.setMaxAge(ONE_HOUR);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);

    return source;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public UserDetailsService userDetailsService() {
    return username -> null;
  }
}
