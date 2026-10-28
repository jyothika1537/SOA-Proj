package com.aegis.auth.service;

import com.aegis.auth.dto.LoginRequest;
import com.aegis.auth.dto.LoginResponse;
import com.aegis.auth.dto.RegisterRequest;
import com.aegis.auth.dto.UserResponse;
import com.aegis.auth.entity.User;
import com.aegis.auth.entity.Role;
import com.aegis.auth.repository.UserRepository;
import com.aegis.auth.security.JwtUtil;
import com.aegis.auth.security.UserPrincipal;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    public UserResponse register(RegisterRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        if (userRepository.findByEmail(email).isPresent()) {
            throw new DataIntegrityViolationException("User with this email already exists");
        }

        User user = User.builder()
                .name(request.getName())
                .email(email)
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(Role.OFFICIAL)
                .build();

        User saved = userRepository.save(user);
        return mapToUserResponse(saved);
    }

    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        String token = jwtUtil.generateToken(principal.getId(), principal.getRole().name());

        return LoginResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(principal.getId())
                .name(principal.getUsername())
                .role(principal.getRole())
                .build();
    }

    public UserResponse getCurrentUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return mapToUserResponse(user);
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
