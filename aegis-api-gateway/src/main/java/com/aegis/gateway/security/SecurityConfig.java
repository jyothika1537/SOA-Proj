package com.aegis.gateway.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    @Value("${jwt.secret}") private String secret;

    @Bean
    @Order(1)
    SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .exceptionHandling(e -> e
                        .authenticationEntryPoint((s, ex) -> { s.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED); return s.getResponse().setComplete(); })
                        .accessDeniedHandler((s, ex) -> { s.getResponse().setStatusCode(HttpStatus.FORBIDDEN); return s.getResponse().setComplete(); }))
                .authorizeExchange(a -> a.pathMatchers("/api/auth/**", "/actuator/health", "/").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/incidents/**", "/api/resources/**", "/api/responses/**").authenticated()
                        .pathMatchers("/api/incidents/**", "/api/resources/**", "/api/responses/**").hasRole("OFFICIAL")
                        .anyExchange().authenticated())
                .addFilterAt(jwtFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    private WebFilter jwtFilter() {
        return (exchange, chain) -> {
            String header = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (header != null && header.startsWith("Bearer ")) {
                try {
                    var claims = Jwts.parser().verifyWith(key()).build().parseSignedClaims(header.substring(7)).getPayload();
                    var auth = new UsernamePasswordAuthenticationToken(claims.getSubject(), null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + claims.get("role", String.class))));
                    var context = org.springframework.security.core.context.SecurityContextHolder.createEmptyContext();
                    context.setAuthentication(auth);
                    exchange.getAttributes().put("aegis.authenticated", auth);
                    return chain.filter(exchange).contextWrite(
                            org.springframework.security.core.context.ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context)));
                } catch (Exception ignored) {
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                }
            }
            return chain.filter(exchange);
        };
    }

    private SecretKey key() { return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)); }
}
