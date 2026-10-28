package com.aegis.response.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration @EnableMethodSecurity @RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtFilter;
    @Bean SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(a -> a.requestMatchers("/actuator/health").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/responses/**").authenticated()
                        .requestMatchers("/api/responses/**").hasRole("OFFICIAL").anyRequest().authenticated())
                .exceptionHandling(e -> e.authenticationEntryPoint((request,response,exception) -> { response.setStatus(401); response.setContentType("application/json"); response.getWriter().write("{\"status\":401,\"error\":\"Unauthorized\"}"); })
                        .accessDeniedHandler((request,response,exception) -> { response.setStatus(403); response.setContentType("application/json"); response.getWriter().write("{\"status\":403,\"error\":\"Forbidden\"}"); }))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class).build();
    }
}
