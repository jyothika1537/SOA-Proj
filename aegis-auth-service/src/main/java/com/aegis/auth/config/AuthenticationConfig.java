package com.aegis.auth.config;

import com.aegis.auth.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

@Configuration
public class AuthenticationConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public AuthenticationConfig(CustomUserDetailsService customUserDetailsService,
                                org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        this.customUserDetailsService = customUserDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }
}
