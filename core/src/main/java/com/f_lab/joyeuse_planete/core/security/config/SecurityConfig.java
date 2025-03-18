package com.f_lab.joyeuse_planete.core.security.config;


import com.f_lab.joyeuse_planete.core.security.filter.JwtFilter;
import jakarta.servlet.Filter;
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



import static org.springframework.security.config.Customizer.withDefaults;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public abstract class SecurityConfig {

  abstract protected Filter jwtFilter();
  abstract protected Filter jwtExceptionFilter();


  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
    return http

        .csrf(AbstractHttpConfigurer::disable)

        .cors(withDefaults())

        .formLogin(AbstractHttpConfigurer::disable)

        .httpBasic(AbstractHttpConfigurer::disable)

        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

        .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(jwtExceptionFilter(), JwtFilter.class)

        .build();
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
