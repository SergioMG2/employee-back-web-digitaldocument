package com.mercadona.employee.digitaldocument.driving.controllers.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfigurer {


  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(auth -> auth
      .anyRequest().permitAll()
    );
    http
      .csrf((csrf) -> csrf.disable());

    return http.build();
  }
}
