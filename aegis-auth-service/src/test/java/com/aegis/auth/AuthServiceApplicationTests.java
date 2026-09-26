package com.aegis.auth;

import com.aegis.auth.dto.LoginRequest;
import com.aegis.auth.dto.RegisterRequest;
import com.aegis.auth.entity.Role;
import com.aegis.auth.entity.User;
import com.aegis.auth.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthServiceApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void registerAndLogin_shouldCreateUserAndReturnToken() throws Exception {
        userRepository.deleteAll();

        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setName("Emergency Official");
        registerRequest.setEmail("official@aegis.com");
        registerRequest.setPassword("Password@123");
        registerRequest.setRole(Role.OFFICIAL);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Emergency Official\",\"email\":\"official@aegis.com\",\"password\":\"Password@123\",\"role\":\"OFFICIAL\"}"))
                .andExpect(status().isCreated());

        User saved = userRepository.findByEmail("official@aegis.com").orElseThrow();
        assertThat(saved.getPasswordHash()).isNotBlank();
        assertThat(passwordEncoder.matches("Password@123", saved.getPasswordHash())).isTrue();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"official@aegis.com\",\"password\":\"Password@123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.userId").value(saved.getId()));
    }

    @Test
    void login_shouldRejectInvalidCredentials() throws Exception {
        userRepository.deleteAll();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"missing@aegis.com\",\"password\":\"Password@123\"}"))
                .andExpect(status().isUnauthorized());
    }
}
